const API_URL = "http://localhost:8080/api";

// Check authentication on page load
document.addEventListener("DOMContentLoaded", () => {
  if (
    window.location.pathname !== "/login.html" &&
    window.location.pathname !== "/register.html" &&
    window.location.pathname !== "/index.html" &&
    window.location.pathname !== "/"
  ) {
    checkAuth();
  }

  // Setup user dropdown toggle
  const userInfoToggle = document.getElementById("userInfoToggle");
  const userDropdown = document.getElementById("userDropdown");
  
  if (userInfoToggle && userDropdown) {
    userInfoToggle.addEventListener("click", (e) => {
      e.stopPropagation();
      userDropdown.classList.toggle("show");
    });

    // Close dropdown when clicking outside
    document.addEventListener("click", (e) => {
      if (!userDropdown.contains(e.target) && e.target !== userInfoToggle) {
        userDropdown.classList.remove("show");
      }
    });
  }

  // Setup logout button
  const logoutBtn = document.getElementById("logout");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", (e) => {
      e.preventDefault();
      localStorage.clear();
      window.location.href = "/login.html";
    });
  }

  // Setup delete account button
  const deleteAccountBtn = document.getElementById("deleteAccount");
  if (deleteAccountBtn) {
    deleteAccountBtn.addEventListener("click", async (e) => {
      e.preventDefault();
      
      const confirmed = confirm(
        "Are you sure you want to delete your account? This action cannot be undone and will delete all your data including books, collections, and reading progress."
      );
      
      if (!confirmed) return;
      
      try {
        const response = await fetch(`${API_URL}/users/me`, {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
        
        if (response.ok) {
          alert("Your account has been successfully deleted.");
          localStorage.clear();
          window.location.href = "/login.html";
        } else {
          const error = await response.text();
          alert("Failed to delete account: " + error);
        }
      } catch (error) {
        console.error("Error deleting account:", error);
        alert("An error occurred while deleting your account.");
      }
    });
  }
});

// Check authentication
function checkAuth() {
  const token = localStorage.getItem("token");
  if (!token) {
    window.location.href = "/login.html";
    return false;
  }
  return true;
}

// Get auth headers
function getAuthHeaders() {
  return {
    Authorization: `Bearer ${localStorage.getItem("token")}`,
    "Content-Type": "application/json",
  };
}

// Load Dashboard
async function loadDashboard() {
  const username = localStorage.getItem("username");
  document.getElementById("username").textContent = username;

  try {
    // Load user library stats
    const response = await fetch(`${API_URL}/library`, {
      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error("Failed to load dashboard:", response.status, errorText);
      throw new Error("Failed to load dashboard data");
    }

    const library = await response.json();
    console.log("Dashboard library data:", library);

    // Calculate stats
    const total = library.length;
    const reading = library.filter(
      (b) => b.progress !== "0%" && b.progress !== "100%"
    ).length;
    const completed = library.filter((b) => b.progress === "100%").length;

    document.getElementById("totalBooks").textContent = total;
    document.getElementById("readingBooks").textContent = reading;
    document.getElementById("completedBooks").textContent = completed;

    // Display recent books
    const recentBooks = library.slice(0, 6);
    displayBooks(recentBooks, "recentBooks");

    // Display currently reading
    const currentlyReading = library
      .filter((b) => b.progress !== "0%" && b.progress !== "100%")
      .slice(0, 6);
    displayBooks(currentlyReading, "continueReading");
    
    // Load collections
    await loadCollections();
  } catch (error) {
    console.error("Error loading dashboard:", error);
  }
}

// Load Community Books
async function loadCommunityBooks() {
  try {
    const response = await fetch(`${API_URL}/books`, {
      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error("Failed to load books:", response.status, errorText);
      throw new Error("Failed to load books");
    }

    const books = await response.json();
    console.log("Loaded books:", books);
    displayCommunityBooks(books, "booksGrid");
  } catch (error) {
    console.error("Error loading books:", error);
    const container = document.getElementById("booksGrid");
    if (container) {
      container.innerHTML = `<p style="color: red;">Error loading books: ${error.message}. Please check console for details.</p>`;
    }
  }
}

// Display Community Books
function displayCommunityBooks(books, containerId) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";
  
  const currentUserId = localStorage.getItem("userId"); // Get current user ID

  books.forEach(async (book) => {
    const card = document.createElement("div");
    card.className = "book-card";
    
    const isOwner = book.userId && currentUserId && parseInt(book.userId) === parseInt(currentUserId);
    
    // Fetch rating for this book
    let ratingHTML = '';
    try {
      const ratingResponse = await fetch(`${API_URL}/books/${book.bookId}/rating`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });
      
      if (ratingResponse.ok) {
        const ratingData = await ratingResponse.json();
        const avgRating = parseFloat(ratingData.AverageRating || 0);
        const totalRatings = ratingData.TotalRatings || 0;
        
        if (totalRatings > 0) {
          const stars = "⭐".repeat(Math.round(avgRating));
          ratingHTML = `<div class="book-rating">${stars} ${avgRating.toFixed(1)} (${totalRatings})</div>`;
        } else {
          ratingHTML = '<div class="book-rating">No ratings yet</div>';
        }
      }
    } catch (error) {
      console.error("Error fetching rating:", error);
    }
    
    card.innerHTML = `
            <div class="book-cover">📖</div>
            <div class="book-title">${book.name}</div>
            <div class="book-info">
                ${book.language || "Unknown"} • ${book.format || "Unknown"}
            </div>
            ${ratingHTML}
            <button onclick="showBookInfo(${book.bookId}, false)" class="btn btn-small" style="background: #17a2b8;">
                ℹ️ Info
            </button>
            <button onclick="addBookToCollection(${book.bookId})" class="btn btn-small" style="background: #6c757d;">
                📁 Add to Collection
            </button>
            ${isOwner ? `
                <button onclick="editBook(${book.bookId})" class="btn btn-small btn-warning">
                    Edit
                </button>
                <button onclick="deleteBook(${book.bookId})" class="btn btn-small btn-danger">
                    Delete
                </button>
            ` : `
                <button onclick="addToLibrary(${book.bookId})" class="btn btn-small">
                    Add to Library
                </button>
            `}
            <button onclick="readBook(${book.bookId})" class="btn btn-small">
                Read
            </button>
        `;
    container.appendChild(card);
  });
}

// Display Books
function displayBooks(books, containerId) {
  const container = document.getElementById(containerId);
  container.innerHTML = "";

  books.forEach((book) => {
    const card = document.createElement("div");
    card.className = "book-card";
    card.innerHTML = `
            <div class="book-cover">📖</div>
            <div class="book-title">${
              book.book?.name || book.name || "Unknown"
            }</div>
            <div class="book-info">
                Progress: ${book.progress || "0%"}<br>
                Rating: ${"⭐".repeat(book.userRating || 0)}
            </div>
            <button onclick="showBookInfo(${
              book.book?.bookId || book.bookId
            }, true)" class="btn btn-small" style="background: #17a2b8;">
                ℹ️ Info
            </button>
            <button onclick="readBook(${
              book.book?.bookId || book.bookId
            })" class="btn btn-small">
                Read
            </button>
            <button onclick="deleteFromLibrary(${
              book.book?.bookId || book.bookId
            })" class="btn btn-small" style="background: #dc3545;">
                🗑️ Remove
            </button>
        `;
    container.appendChild(card);
  });

  if (books.length === 0) {
    container.innerHTML = "<p>No books found</p>";
  }
}

// Load User Library
async function loadUserLibrary() {
  try {
    const response = await fetch(`${API_URL}/library`, {
      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error("Failed to load library:", response.status, errorText);
      throw new Error("Failed to load library");
    }

    const library = await response.json();
    console.log("Loaded library:", library);
    displayBooks(library, "libraryGrid");
  } catch (error) {
    console.error("Error loading library:", error);
    const container = document.getElementById("libraryGrid");
    if (container) {
      container.innerHTML = `<p style="color: red;">Error loading library: ${error.message}. Please check console for details.</p>`;
    }
  }
}

// Filter Library
async function filterLibrary(filter) {
  // Update active tab
  document.querySelectorAll(".tab-btn").forEach((btn) => {
    btn.classList.remove("active");
  });
  event.target.classList.add("active");

  try {
    const response = await fetch(`${API_URL}/library`, {
      headers: getAuthHeaders(),
    });

    if (!response.ok) throw new Error("Failed to load library");

    let library = await response.json();

    // Apply filter
    if (filter === "reading") {
      library = library.filter(
        (b) => b.progress !== "0%" && b.progress !== "100%"
      );
    } else if (filter === "completed") {
      library = library.filter((b) => b.progress === "100%");
    } else if (filter === "not-started") {
      library = library.filter((b) => b.progress === "0%");
    }

    displayBooks(library, "libraryGrid");
  } catch (error) {
    console.error("Error filtering library:", error);
  }
}

// Add to Library
async function addToLibrary(bookId) {
  try {
    const response = await fetch(`${API_URL}/library/${bookId}`, {
      method: "POST",
      headers: getAuthHeaders(),
    });

    if (!response.ok) throw new Error("Failed to add book");

    alert("Book added to your library!");
  } catch (error) {
    console.error("Error adding book:", error);
    alert("Failed to add book to library");
  }
}

// Delete Book from Library
async function deleteFromLibrary(bookId) {
  if (!confirm("Are you sure you want to remove this book from your library?")) {
    return;
  }
  
  try {
    const response = await fetch(`${API_URL}/library/${bookId}`, {
      method: "DELETE",
      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || "Failed to remove book");
    }

    alert("Book removed from your library!");
    
    // Reload library
    if (typeof loadUserLibrary === 'function') {
      loadUserLibrary();
    } else if (typeof loadDashboard === 'function') {
      loadDashboard();
    }
  } catch (error) {
    console.error("Error removing book:", error);
    alert("Failed to remove book from library: " + error.message);
  }
}

// Read Book
function readBook(bookId) {
  window.location.href = `/reader.html?bookId=${bookId}`;
}

// Search Books
async function searchBooks() {
  const query = document.getElementById("searchInput").value;

  try {
    const response = await fetch(
      `${API_URL}/books/search?q=${encodeURIComponent(query)}`,
      {
        headers: getAuthHeaders(),
      }
    );

    if (!response.ok) throw new Error("Search failed");

    const books = await response.json();
    displayCommunityBooks(books, "booksGrid");
  } catch (error) {
    console.error("Error searching books:", error);
  }
}

// Apply Filters
async function applyFilters() {
  const language = document.getElementById("languageFilter").value;
  const format = document.getElementById("formatFilter").value;

  // Reload and filter books
  loadCommunityBooks();
}

// Edit Book
function editBook(bookId) {
  const newTitle = prompt("Enter new book title:");
  if (!newTitle) return;
  
  const newLanguage = prompt("Enter language (optional):");
  
  updateBookDetails(bookId, newTitle, newLanguage);
}

async function updateBookDetails(bookId, title, language) {
  try {
    const response = await fetch(`${API_URL}/books/${bookId}`, {
      method: "PUT",
      headers: getAuthHeaders(),
      body: JSON.stringify({
        name: title,
        language: language || "Unknown",
        format: "PDF" // Keep existing format
      })
    });

    if (!response.ok) throw new Error("Failed to update book");

    alert("Book updated successfully!");
    loadCommunityBooks(); // Reload books
  } catch (error) {
    console.error("Error updating book:", error);
    alert("Failed to update book");
  }
}

// Delete Book
async function deleteBook(bookId) {
  if (!confirm("Are you sure you want to delete this book? This action cannot be undone.")) {
    return;
  }

  try {
    const response = await fetch(`${API_URL}/books/${bookId}`, {
      method: "DELETE",
      headers: getAuthHeaders(),
    });

    if (!response.ok) throw new Error("Failed to delete book");

    alert("Book deleted successfully!");
    loadCommunityBooks(); // Reload books
  } catch (error) {
    console.error("Error deleting book:", error);
    alert("Failed to delete book");
  }
}

// Show Book Info Modal
async function showBookInfo(bookId, isInLibrary) {
  try {
    console.log("Fetching book info for bookId:", bookId, "isInLibrary:", isInLibrary);
    
    // Fetch book details with authors and genres
    const bookResponse = await fetch(`${API_URL}/books/${bookId}/details`, {
      headers: getAuthHeaders(),
    });
    
    if (!bookResponse.ok) {
      const errorText = await bookResponse.text();
      console.error("Failed to load book:", bookResponse.status, errorText);
      throw new Error("Failed to load book");
    }
    
    const book = await bookResponse.json();
    console.log("Book details:", book);
    
    // Fetch rating
    const ratingResponse = await fetch(`${API_URL}/books/${bookId}/rating`, {
      headers: getAuthHeaders(),
    });
    
    let avgRating = 0;
    let totalRatings = 0;
    
    if (ratingResponse.ok) {
      const ratingData = await ratingResponse.json();
      avgRating = parseFloat(ratingData.AverageRating || 0);
      totalRatings = ratingData.TotalRatings || 0;
      console.log("Rating data:", ratingData);
    }
    
    // Fetch user's library info if in library
    let userBook = null;
    if (isInLibrary) {
      const libraryResponse = await fetch(`${API_URL}/library`, {
        headers: getAuthHeaders(),
      });
      
      if (libraryResponse.ok) {
        const library = await libraryResponse.json();
        userBook = library.find(b => b.bookId === bookId);
        console.log("User book data:", userBook);
      }
    }
    
    // Build modal content
    document.getElementById('modalBookTitle').textContent = book.name;
    
    let content = `
      <div class="book-detail-item">
        <label>Author(s):</label>
        <div class="value">${book.authors || 'Unknown'}</div>
      </div>
      <div class="book-detail-item">
        <label>Genre(s):</label>
        <div class="value">${book.genres || 'Not specified'}</div>
      </div>
      <div class="book-detail-item">
        <label>Publisher:</label>
        <div class="value">${book.publisher || 'Unknown'}</div>
      </div>
      <div class="book-detail-item">
        <label>Language:</label>
        <div class="value">${book.language || 'Unknown'}</div>
      </div>
      <div class="book-detail-item">
        <label>Format:</label>
        <div class="value">${book.format || 'Unknown'}</div>
      </div>
      <div class="book-detail-item">
        <label>File Size:</label>
        <div class="value">${book.fileSize || 'Unknown'}</div>
      </div>
      <div class="book-detail-item">
        <label>Average Rating:</label>
        <div class="value">${totalRatings > 0 ? `${"⭐".repeat(Math.round(avgRating))} ${avgRating.toFixed(1)} (${totalRatings} ratings)` : 'No ratings yet'}</div>
      </div>
    `;
    
    // If book is in user's library, show editable fields
    if (isInLibrary && userBook) {
      content += `
        <div class="edit-section">
          <h3>Your Library Info</h3>
          
          <div class="book-detail-item">
            <label>Your Rating:</label>
            <div class="stars-input" id="starsInput">
              ${[1, 2, 3, 4, 5].map(i => 
                `<span class="star ${i <= (userBook.userRating || 0) ? 'active' : ''}" 
                      data-rating="${i}" 
                      onclick="selectRating(${i}, ${bookId})">★</span>`
              ).join('')}
            </div>
            <span id="currentRating">${userBook.userRating ? `${userBook.userRating}/5` : 'Not rated'}</span>
          </div>
          
          <div class="book-detail-item">
            <label>Progress:</label>
            <input type="text" id="progressInput" value="${userBook.progress || '0'}" 
                   style="width: 100px; padding: 5px; border: 1px solid #ddd; border-radius: 3px;">
            <button onclick="updateBookProgress(${bookId})" class="btn btn-small" style="margin-left: 10px;">
              Update Progress
            </button>
          </div>
          
          <div class="book-detail-item">
            <label>Added Date:</label>
            <div class="value">${userBook.lastReadDate || 'Unknown'}</div>
          </div>
        </div>
      `;
    }
    
    document.getElementById('bookInfoContent').innerHTML = content;
    
    // Show modal with proper centering
    const modal = document.getElementById('bookInfoModal');
    modal.classList.remove('hidden');
    modal.style.display = 'flex';
    
  } catch (error) {
    console.error("Error loading book info:", error);
    alert("Failed to load book information: " + error.message + ". Check console for details.");
  }
}

function closeBookInfo() {
  const modal = document.getElementById('bookInfoModal');
  modal.classList.add('hidden');
  modal.style.display = 'none';
}

// Close modal when clicking outside
window.onclick = function(event) {
  const modal = document.getElementById('bookInfoModal');
  const collectionModal = document.getElementById('collectionBooksModal');
  
  if (event.target === modal) {
    closeBookInfo();
  }
  
  if (collectionModal && event.target === collectionModal) {
    closeCollectionBooksModal();
  }
}

async function selectRating(rating, bookId) {
  try {
    const response = await fetch(`${API_URL}/library/${bookId}/rating`, {
      method: 'PUT',
      headers: {
        ...getAuthHeaders(),
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ rating }),
    });
    
    if (response.ok) {
      const data = await response.json();
      
      // Update stars display
      document.querySelectorAll('#starsInput .star').forEach((star, index) => {
        if (index < rating) {
          star.classList.add('active');
        } else {
          star.classList.remove('active');
        }
      });
      
      document.getElementById('currentRating').textContent = `${rating}/5`;
      alert('Rating updated successfully!');
    } else {
      const error = await response.json();
      alert(error.error || 'Failed to update rating');
    }
  } catch (error) {
    console.error("Error updating rating:", error);
    alert('Failed to update rating');
  }
}

async function updateBookProgress(bookId) {
  const progressInput = document.getElementById('progressInput');
  if (!progressInput) {
    alert('Progress input not found');
    return;
  }
  
  const progress = progressInput.value.trim();
  
  // Validate progress format (should be like "50%" or "50" or "100%")
  if (!progress) {
    alert('Please enter a progress value');
    return;
  }
  
  // Ensure it has % sign
  const formattedProgress = progress.includes('%') ? progress : progress + '%';
  
  console.log("Updating progress for bookId:", bookId, "to:", formattedProgress);
  
  try {
    const response = await fetch(`${API_URL}/library/${bookId}/progress`, {
      method: 'PUT',
      headers: {
        ...getAuthHeaders(),
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ progress: formattedProgress }),
    });
    
    if (response.ok) {
      alert('Progress updated successfully!');
      console.log("Progress updated successfully");
      // Close the modal
      closeBookInfo();
      // Reload library if on library page
      if (typeof loadUserLibrary === 'function') {
        loadUserLibrary();
      }
      // Reload dashboard if on dashboard page
      if (typeof loadDashboard === 'function') {
        loadDashboard();
      }
    } else {
      const errorText = await response.text();
      console.error("Failed to update progress:", response.status, errorText);
      alert('Failed to update progress: ' + errorText);
    }
  } catch (error) {
    console.error("Error updating progress:", error);
    alert('Failed to update progress: ' + error.message);
  }
}

// Collection Management
async function loadCollections() {
  try {
    const response = await fetch(`${API_URL}/collections`, {
      headers: getAuthHeaders(),
    });
    
    if (response.ok) {
      const collections = await response.json();
      displayCollections(collections);
      
      // Update collection count in dashboard
      const collectionCountEl = document.getElementById('totalCollections');
      if (collectionCountEl) {
        collectionCountEl.textContent = collections.length;
      }
    }
  } catch (error) {
    console.error("Error loading collections:", error);
  }
}

function displayCollections(collections) {
  const container = document.getElementById('collectionsGrid');
  if (!container) return;
  
  container.innerHTML = '';
  
  if (collections.length === 0) {
    container.innerHTML = '<p>No collections yet. Create your first collection!</p>';
    return;
  }
  
  collections.forEach(collection => {
    const card = document.createElement('div');
    card.className = 'collection-card';
    
    card.innerHTML = `
      <div class="collection-name">${collection.collectionName}</div>
      <div class="collection-count">${collection.bookCount} book${collection.bookCount !== 1 ? 's' : ''}</div>
      <div class="collection-date">Created: ${new Date(collection.createdDate).toLocaleDateString()}</div>
      <div class="collection-actions">
        <button class="btn btn-small view-collection-btn" data-collection-id="${collection.collectionId}" data-collection-name="${collection.collectionName}">
          View Books
        </button>
        <button class="btn btn-small btn-danger delete-collection-btn" data-collection-id="${collection.collectionId}">
          Delete
        </button>
      </div>
    `;
    
    // Add event listeners
    const viewBtn = card.querySelector('.view-collection-btn');
    viewBtn.addEventListener('click', () => {
      viewCollection(collection.collectionId, collection.collectionName);
    });
    
    const deleteBtn = card.querySelector('.delete-collection-btn');
    deleteBtn.addEventListener('click', () => {
      deleteCollectionConfirm(collection.collectionId);
    });
    
    container.appendChild(card);
  });
}

async function createCollection() {
  const collectionName = prompt('Enter collection name:');
  
  if (!collectionName || collectionName.trim() === '') {
    return;
  }
  
  try {
    const response = await fetch(`${API_URL}/collections`, {
      method: 'POST',
      headers: {
        ...getAuthHeaders(),
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ collectionName: collectionName.trim() }),
    });
    
    if (response.ok) {
      alert('Collection created successfully!');
      loadCollections();
    } else {
      const error = await response.json();
      alert(error.error || 'Failed to create collection');
    }
  } catch (error) {
    console.error("Error creating collection:", error);
    alert('Failed to create collection');
  }
}

async function deleteCollectionConfirm(collectionId) {
  console.log("Delete collection called for ID:", collectionId);
  
  if (!confirm('Are you sure you want to delete this collection? This will not delete the books.')) {
    return;
  }
  
  try {
    const response = await fetch(`${API_URL}/collections/${collectionId}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    });
    
    if (response.ok) {
      alert('Collection deleted successfully!');
      console.log("Collection deleted successfully");
      loadCollections();
    } else {
      const errorText = await response.text();
      console.error("Failed to delete collection:", response.status, errorText);
      alert('Failed to delete collection: ' + errorText);
    }
  } catch (error) {
    console.error("Error deleting collection:", error);
    alert('Failed to delete collection: ' + error.message);
  }
}

async function viewCollection(collectionId, collectionName) {
  console.log("viewCollection called with:", collectionId, collectionName);
  try {
    const url = `${API_URL}/collections/${collectionId}/books`;
    console.log("Fetching from:", url);
    
    const response = await fetch(url, {
      headers: getAuthHeaders(),
    });
    
    console.log("Response status:", response.status);
    
    if (response.ok) {
      const books = await response.json();
      console.log("Books received:", books);
      
      // Update modal title
      document.getElementById('collectionModalTitle').textContent = `📚 ${collectionName}`;
      
      const container = document.getElementById('collectionBooksContent');
      
      if (books.length === 0) {
        container.innerHTML = '<p style="text-align: center; padding: 2rem; color: #999;">This collection is empty.</p>';
      } else {
        // Display books in a grid
        container.innerHTML = books.map(book => `
          <div class="book-card">
            <div class="book-cover">
              <span class="book-icon">📖</span>
            </div>
            <div class="book-info">
              <h3>${book.name || 'Untitled'}</h3>
              <p class="author">${book.authors || 'Unknown Author'}</p>
              <p class="meta">
                <span>🌐 ${book.language || 'Unknown'}</span>
                <span>📄 ${book.format || 'Unknown'}</span>
              </p>
              <div class="book-actions" style="margin-top: 0.5rem;">
                <button onclick="readBook(${book.bookId})" class="btn btn-small">
                  📖 Read
                </button>
                <button onclick="showBookInfo(${book.bookId}, false)" class="btn btn-small" style="background: #17a2b8;">
                  ℹ️ Info
                </button>
              </div>
            </div>
          </div>
        `).join('');
      }
      
      // Show modal with proper centering
      const modal = document.getElementById('collectionBooksModal');
      modal.classList.remove('hidden');
      modal.style.display = 'flex';
    } else {
      const errorText = await response.text();
      console.error("Error response:", errorText);
      alert('Failed to load collection books');
    }
  } catch (error) {
    console.error("Error viewing collection:", error);
    alert('Failed to load collection books: ' + error.message);
  }
}

function closeCollectionBooksModal() {
  const modal = document.getElementById('collectionBooksModal');
  modal.classList.add('hidden');
  modal.style.display = 'none';
}

async function addBookToCollection(bookId) {
  try {
    // Load user's collections
    const response = await fetch(`${API_URL}/collections`, {
      headers: getAuthHeaders(),
    });
    
    if (!response.ok) {
      alert('Failed to load collections');
      return;
    }
    
    const collections = await response.json();
    
    if (collections.length === 0) {
      if (confirm('You have no collections. Create one now?')) {
        await createCollection();
        // After creating, try again
        return addBookToCollection(bookId);
      }
      return;
    }
    
    // Show selection dialog (simple prompt for now)
    const collectionList = collections.map((c, i) => `${i + 1}. ${c.collectionName}`).join('\n');
    const selection = prompt(`Select a collection:\n\n${collectionList}\n\nEnter number:`);
    
    if (!selection) return;
    
    const index = parseInt(selection) - 1;
    if (index < 0 || index >= collections.length) {
      alert('Invalid selection');
      return;
    }
    
    const collectionId = collections[index].collectionId;
    
    // Add book to collection
    const addResponse = await fetch(`${API_URL}/collections/${collectionId}/books/${bookId}`, {
      method: 'POST',
      headers: getAuthHeaders(),
    });
    
    if (addResponse.ok) {
      alert('Book added to collection!');
      loadCollections(); // Refresh to update counts
    } else {
      const error = await addResponse.json();
      alert(error.error || 'Failed to add book to collection');
    }
  } catch (error) {
    console.error("Error adding book to collection:", error);
    alert('Failed to add book to collection');
  }
}
