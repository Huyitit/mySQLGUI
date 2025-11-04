const API_URL = "http://localhost:8080/api";

let currentBookId;
let currentPage = 1;
let totalPages = 1;
let pdfDoc = null;
let userRating = 0;

// Get book ID from URL
const urlParams = new URLSearchParams(window.location.search);
currentBookId = urlParams.get("bookId");

// Load book on page load
document.addEventListener("DOMContentLoaded", async () => {
  if (!currentBookId) {
    alert("No book specified");
    window.location.href = "/library.html";
    return;
  }

  await loadBook();
  initializeRatingStars();
  await loadRating();
  
  // Auto-save progress when leaving the page
  window.addEventListener('beforeunload', async (e) => {
    await saveReadingProgress();
  });
  
  // Also save progress periodically (every 30 seconds)
  setInterval(() => {
    saveReadingProgress();
  }, 30000);
});

// Load book details and file
async function loadBook() {
  try {
    // Get book details
    const response = await fetch(`${API_URL}/books/${currentBookId}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });

    if (!response.ok) throw new Error("Failed to load book");

    const book = await response.json();
    document.getElementById("bookTitle").textContent = book.name;

    // Load book file based on format
    if (book.format === "PDF") {
      await loadPDF(currentBookId);
    } else if (book.format === "EPUB") {
      await loadEPUB(currentBookId);
    } else {
      alert("Unsupported format: " + book.format);
    }

    // Load bookmarks
    await loadBookmarks();
  } catch (error) {
    console.error("Error loading book:", error);
    alert("Failed to load book");
  }
}

// Load PDF
async function loadPDF(bookId) {
  const pdfViewer = document.getElementById("pdfViewer");
  pdfViewer.style.display = "block";

  const url = `${API_URL}/books/read/${bookId}`;

  try {
    // Configure PDF.js worker from CDN
    pdfjsLib.GlobalWorkerOptions.workerSrc = 'https://cdnjs.cloudflare.com/ajax/libs/pdf.js/3.11.174/pdf.worker.min.js';

    const loadingTask = pdfjsLib.getDocument({
      url: url,
      httpHeaders: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });

    pdfDoc = await loadingTask.promise;
    totalPages = pdfDoc.numPages;
    
    // Load saved progress and start from that page
    const savedPage = await loadSavedProgress();
    const startPage = savedPage > 0 && savedPage <= totalPages ? savedPage : 1;
    
    console.log(`Starting from page ${startPage} of ${totalPages}`);
    await renderPage(startPage);
    updatePageInfo();
  } catch (error) {
    console.error("Error loading PDF:", error);
    alert("Failed to load PDF file");
  }
}

// Render PDF page
async function renderPage(pageNum) {
  if (!pdfDoc) return;

  try {
    const page = await pdfDoc.getPage(pageNum);
    const canvas = document.getElementById("pdfCanvas");
    const context = canvas.getContext("2d");

    const viewport = page.getViewport({ scale: 1.5 });
    canvas.height = viewport.height;
    canvas.width = viewport.width;

    const renderContext = {
      canvasContext: context,
      viewport: viewport,
    };

    await page.render(renderContext).promise;
    currentPage = pageNum;
    updatePageInfo();
  } catch (error) {
    console.error("Error rendering page:", error);
  }
}

// Update page info display
function updatePageInfo() {
  document.getElementById("pageInfo").textContent = `Page ${currentPage} of ${totalPages}`;
}

// Load EPUB
async function loadEPUB(bookId) {
  const epubViewer = document.getElementById("epubViewer");
  epubViewer.style.display = "block";

  const url = `${API_URL}/books/read/${bookId}`;

  try {
    const book = ePub(url, {
      requestHeaders: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });

    const rendition = book.renderTo("epubViewer", {
      width: "100%",
      height: "100%",
      spread: "none",
    });

    await rendition.display();

    // Update total pages estimation (EPUB doesn't have fixed pages)
    totalPages = 0; // EPUB uses locations instead of pages
    updatePageInfo();

    // Handle navigation
    document.addEventListener("keydown", (e) => {
      if (e.key === "ArrowLeft") {
        rendition.prev();
      } else if (e.key === "ArrowRight") {
        rendition.next();
      }
    });
  } catch (error) {
    console.error("Error loading EPUB:", error);
    alert("Failed to load EPUB file");
  }
}

// Navigation
function previousPage() {
  if (currentPage > 1) {
    renderPage(currentPage - 1);
    // Save progress after page change
    saveReadingProgress();
  }
}

function nextPage() {
  if (currentPage < totalPages) {
    renderPage(currentPage + 1);
    // Save progress after page change
    saveReadingProgress();
  }
}

function goBack() {
  window.history.back();
}

// Update reading progress
async function updateProgress() {
  const progress = Math.round((currentPage / totalPages) * 100);

  try {
    await fetch(`${API_URL}/library/${currentBookId}/progress`, {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ progress: `${progress}%` }),
    });
  } catch (error) {
    console.error("Error updating progress:", error);
  }
}

// Bookmarks
async function loadBookmarks() {
  try {
    const response = await fetch(
      `${API_URL}/bookmarks?bookId=${currentBookId}`,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      }
    );

    if (!response.ok) throw new Error("Failed to load bookmarks");

    const bookmarks = await response.json();
    displayBookmarks(bookmarks);
  } catch (error) {
    console.error("Error loading bookmarks:", error);
  }
}

function displayBookmarks(bookmarks) {
  const list = document.getElementById("bookmarkList");
  list.innerHTML = "";

  bookmarks.forEach((bookmark) => {
    const item = document.createElement("div");
    item.className = "bookmark-item";
    item.innerHTML = `
            <div>
                <strong>${bookmark.bookmarkName || "Unnamed"}</strong><br>
                <small>${bookmark.location}</small>
            </div>
            <button onclick="goToBookmark('${bookmark.location}')">Go</button>
        `;
    list.appendChild(item);
  });
}

function toggleBookmarks() {
  const panel = document.getElementById("bookmarkPanel");
  panel.style.display = panel.style.display === "none" ? "block" : "none";
}

async function addBookmark() {
  const name = prompt("Bookmark name:");
  if (!name) return;

  try {
    await fetch(`${API_URL}/bookmarks`, {
      method: "POST",
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        bookmarkName: name,
        location: `Page ${currentPage}`,
        bookId: currentBookId,
      }),
    });

    await loadBookmarks();
  } catch (error) {
    console.error("Error adding bookmark:", error);
    alert("Failed to add bookmark");
  }
}

function goToBookmark(location) {
  // Parse location and navigate
  const pageMatch = location.match(/Page (\d+)/);
  if (pageMatch) {
    currentPage = parseInt(pageMatch[1]);
    renderPage(currentPage);
  }
}

function goBack() {
  window.history.back();
}

// Initialize rating stars
function initializeRatingStars() {
  const stars = document.querySelectorAll('.star');
  
  stars.forEach(star => {
    star.addEventListener('click', async () => {
      const rating = parseInt(star.getAttribute('data-rating'));
      await submitRating(rating);
    });
    
    star.addEventListener('mouseenter', () => {
      const rating = parseInt(star.getAttribute('data-rating'));
      highlightStars(rating);
    });
  });
  
  document.querySelector('.stars').addEventListener('mouseleave', () => {
    highlightStars(userRating);
  });
}

function highlightStars(rating) {
  const stars = document.querySelectorAll('.star');
  stars.forEach((star, index) => {
    if (index < rating) {
      star.classList.add('active');
    } else {
      star.classList.remove('active');
    }
  });
}

async function loadRating() {
  try {
    const response = await fetch(`${API_URL}/books/${currentBookId}`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
    
    if (response.ok) {
      const book = await response.json();
      
      // Check if user has this book in library
      const libraryResponse = await fetch(`${API_URL}/library`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      
      if (libraryResponse.ok) {
        const library = await libraryResponse.json();
        const userBook = library.find(b => b.bookId === parseInt(currentBookId));
        
        if (userBook && userBook.userRating) {
          userRating = userBook.userRating;
          highlightStars(userRating);
        }
      }
      
      // Load and display average rating
      await loadAverageRating();
    }
  } catch (error) {
    console.error("Error loading rating:", error);
  }
}

async function loadAverageRating() {
  try {
    const response = await fetch(`${API_URL}/books/${currentBookId}/rating`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
    
    if (response.ok) {
      const data = await response.json();
      const avgElement = document.getElementById('avgRating');
      
      if (data.totalRatings > 0) {
        avgElement.textContent = `Avg: ${parseFloat(data.averageRating).toFixed(1)}/5 (${data.totalRatings} ratings)`;
      } else {
        avgElement.textContent = 'No ratings yet';
      }
    }
  } catch (error) {
    console.error("Error loading average rating:", error);
  }
}

async function submitRating(rating) {
  try {
    // First check if book is in library, if not add it
    const libraryResponse = await fetch(`${API_URL}/library`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
    
    if (libraryResponse.ok) {
      const library = await libraryResponse.json();
      const bookInLibrary = library.find(b => b.bookId === parseInt(currentBookId));
      
      if (!bookInLibrary) {
        // Add to library first
        await fetch(`${API_URL}/library/${currentBookId}`, {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
      }
    }
    
    // Submit rating
    const response = await fetch(`${API_URL}/library/${currentBookId}/rating`, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ rating }),
    });
    
    if (response.ok) {
      const data = await response.json();
      userRating = rating;
      highlightStars(userRating);
      
      // Update average rating display
      const avgElement = document.getElementById('avgRating');
      avgElement.textContent = `Avg: ${parseFloat(data.averageRating).toFixed(1)}/5 (${data.totalRatings} ratings)`;
      
      alert('Rating submitted successfully!');
    } else {
      const error = await response.json();
      alert(error.error || 'Failed to submit rating');
    }
  } catch (error) {
    console.error("Error submitting rating:", error);
    alert('Failed to submit rating');
  }
}

// Load saved reading progress and calculate the page number
async function loadSavedProgress() {
  try {
    // Get user's library
    const response = await fetch(`${API_URL}/library`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
    
    if (!response.ok) return 0;
    
    const library = await response.json();
    const bookInLibrary = library.find(b => b.bookId === parseInt(currentBookId));
    
    if (!bookInLibrary || !bookInLibrary.progress) {
      console.log('No saved progress found, starting from page 1');
      return 0;
    }
    
    // Parse progress percentage (e.g., "25%" -> 25)
    const progressPercent = parseInt(bookInLibrary.progress.replace('%', ''));
    
    if (isNaN(progressPercent) || progressPercent === 0) {
      return 0;
    }
    
    // Calculate page number from progress percentage
    // We need totalPages to be set first, so this should be called after PDF is loaded
    const savedPage = Math.max(1, Math.round((progressPercent / 100) * totalPages));
    
    console.log(`Loaded saved progress: ${bookInLibrary.progress} -> Page ${savedPage}`);
    return savedPage;
    
  } catch (error) {
    console.error("Error loading saved progress:", error);
    return 0;
  }
}

// Save reading progress automatically
async function saveReadingProgress() {
  if (!currentBookId || totalPages === 0) return;
  
  try {
    // Calculate progress percentage
    const progressPercentage = Math.round((currentPage / totalPages) * 100);
    const progress = `${progressPercentage}%`;
    
    console.log(`Auto-saving progress: Page ${currentPage}/${totalPages} = ${progress}`);
    
    // First check if book is in library, if not add it
    const libraryResponse = await fetch(`${API_URL}/library`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
    
    if (libraryResponse.ok) {
      const library = await libraryResponse.json();
      const bookInLibrary = library.find(b => b.bookId === parseInt(currentBookId));
      
      if (!bookInLibrary) {
        // Add to library first
        await fetch(`${API_URL}/library/${currentBookId}`, {
          method: 'POST',
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
      }
    }
    
    // Update progress
    const response = await fetch(`${API_URL}/library/${currentBookId}/progress`, {
      method: 'PUT',
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ progress }),
    });
    
    if (response.ok) {
      console.log('Progress saved successfully:', progress);
    } else {
      console.error('Failed to save progress:', response.status);
    }
  } catch (error) {
    console.error("Error saving progress:", error);
  }
}
