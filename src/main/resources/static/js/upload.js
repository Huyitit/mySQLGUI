const API_URL = "http://localhost:8080/api";

console.log("Upload.js loaded!");
console.log("Genre input element:", document.getElementById("genres"));
console.log("Genre suggestions element:", document.getElementById("genreSuggestions"));

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

// Author autocomplete
const authorInput = document.getElementById("author");
const authorSuggestions = document.getElementById("authorSuggestions");
let authorDebounceTimer;

if (authorInput) {
  authorInput.addEventListener("input", (e) => {
    clearTimeout(authorDebounceTimer);
    const query = e.target.value.trim();
    
    if (query.length < 2) {
      authorSuggestions.classList.remove("show");
      return;
    }
    
    authorDebounceTimer = setTimeout(async () => {
      try {
        const response = await fetch(`${API_URL}/authors/search?q=${encodeURIComponent(query)}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
        
        if (response.ok) {
          const authors = await response.json();
          displayAuthorSuggestions(authors);
        }
      } catch (error) {
        console.error("Error fetching authors:", error);
      }
    }, 300);
  });
  
  // Close suggestions when clicking outside
  document.addEventListener("click", (e) => {
    if (e.target !== authorInput && e.target !== authorSuggestions) {
      authorSuggestions.classList.remove("show");
    }
  });
}

function displayAuthorSuggestions(authors) {
  if (authors.length === 0) {
    authorSuggestions.classList.remove("show");
    return;
  }
  
  authorSuggestions.innerHTML = authors
    .map(author => `
      <div class="suggestion-item" onclick="selectAuthor('${author.authorName.replace(/'/g, "\\'")}')">
        ${author.authorName}
      </div>
    `)
    .join('');
  
  authorSuggestions.classList.add("show");
}

function selectAuthor(authorName) {
  document.getElementById("author").value = authorName;
  authorSuggestions.classList.remove("show");
}

// Publisher autocomplete
const publisherInput = document.getElementById("publisher");
const publisherSuggestions = document.getElementById("publisherSuggestions");
let publisherDebounceTimer;

if (publisherInput) {
  publisherInput.addEventListener("input", (e) => {
    clearTimeout(publisherDebounceTimer);
    const query = e.target.value.trim();
    
    if (query.length < 2) {
      publisherSuggestions.classList.remove("show");
      return;
    }
    
    publisherDebounceTimer = setTimeout(async () => {
      try {
        const response = await fetch(`${API_URL}/publishers/search?q=${encodeURIComponent(query)}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
        
        if (response.ok) {
          const publishers = await response.json();
          displayPublisherSuggestions(publishers);
        }
      } catch (error) {
        console.error("Error fetching publishers:", error);
      }
    }, 300);
  });
  
  // Close suggestions when clicking outside
  document.addEventListener("click", (e) => {
    if (e.target !== publisherInput && e.target !== publisherSuggestions) {
      publisherSuggestions.classList.remove("show");
    }
  });
}

function displayPublisherSuggestions(publishers) {
  if (publishers.length === 0) {
    publisherSuggestions.classList.remove("show");
    return;
  }
  
  publisherSuggestions.innerHTML = publishers
    .map(publisher => `
      <div class="suggestion-item" onclick="selectPublisher('${publisher.publisherName.replace(/'/g, "\\'")}')">
        ${publisher.publisherName}
      </div>
    `)
    .join('');
  
  publisherSuggestions.classList.add("show");
}

function selectPublisher(publisherName) {
  document.getElementById("publisher").value = publisherName;
  publisherSuggestions.classList.remove("show");
}

// Genre autocomplete and multi-select
const genreInput = document.getElementById("genres");
const genreSuggestions = document.getElementById("genreSuggestions");
const selectedGenresDiv = document.getElementById("selectedGenres");
let selectedGenres = [];
let genreDebounceTimer;
let allGenres = []; // Cache all genres

// Delete account button (if present on the page)
const deleteAccountBtn = document.getElementById('deleteAccount');
if (deleteAccountBtn) {
  deleteAccountBtn.addEventListener('click', async () => {
    const confirmed = confirm('Are you sure you want to permanently delete your account? This cannot be undone.');
    if (!confirmed) return;

    try {
      const resp = await fetch(`${API_URL}/users/me`, {
        method: 'DELETE',
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        }
      });

      if (resp.status === 204) {
        // cleared
        alert('Your account has been deleted. You will be logged out.');
        localStorage.removeItem('token');
        window.location.href = '/index.html';
      } else if (resp.status === 401 || resp.status === 403) {
        alert('You must be logged in to delete your account.');
      } else {
        const text = await resp.text();
        console.error('Delete failed', resp.status, text);
        alert('Failed to delete account. Try again later.');
      }
    } catch (err) {
      console.error('Error deleting account:', err);
      alert('Error deleting account. Check console for details.');
    }
  });
}

// Fetch all genres on page load
async function loadAllGenres() {
  try {
    console.log("Loading all genres...");
    console.log("Token:", localStorage.getItem("token") ? "exists" : "missing");
    
    // Use the /api/genres endpoint to get ALL genres
    const response = await fetch(`${API_URL}/genres`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("token")}`,
      },
    });
    
    console.log("Response status:", response.status);
    
    if (response.ok) {
      allGenres = await response.json();
      console.log("All genres loaded:", allGenres.length, "genres");
      console.log("First few genres:", allGenres.slice(0, 5));
    } else {
      console.error("Failed to load genres. Status:", response.status);
      if (response.status === 401 || response.status === 403) {
        console.error("Authentication required. Please login first.");
      }
    }
  } catch (error) {
    console.error("Error loading genres:", error);
  }
}

// Load genres when page loads
loadAllGenres();

if (genreInput) {
  console.log("Genre input found, adding event listener");
  
  // Show all genres on focus
  genreInput.addEventListener("focus", () => {
    console.log("Genre input focused");
    if (allGenres.length > 0) {
      displayGenreSuggestions(allGenres);
    } else {
      // Load genres if not already loaded
      loadAllGenres().then(() => {
        displayGenreSuggestions(allGenres);
      });
    }
  });
  
  // Filter genres as user types
  genreInput.addEventListener("input", (e) => {
    console.log("Genre input event fired, value:", e.target.value);
    clearTimeout(genreDebounceTimer);
    const query = e.target.value.trim().toLowerCase();
    
    // If empty, show all genres
    if (query.length === 0) {
      displayGenreSuggestions(allGenres);
      return;
    }
    
    // Filter genres locally
    const filteredGenres = allGenres.filter(genre => 
      genre.genreName.toLowerCase().includes(query)
    );
    
    displayGenreSuggestions(filteredGenres);
  });
  
  // Close suggestions when clicking outside
  document.addEventListener("click", (e) => {
    if (e.target !== genreInput && !genreSuggestions.contains(e.target)) {
      genreSuggestions.classList.remove("show");
    }
  });
}

function displayGenreSuggestions(genres) {
  console.log("displayGenreSuggestions called with:", genres);
  // Filter out already selected genres
  const availableGenres = genres.filter(g => 
    !selectedGenres.some(sg => sg.genreId === g.genreId)
  );
  
  console.log("Available genres after filtering:", availableGenres);
  
  if (availableGenres.length === 0) {
    genreSuggestions.innerHTML = '<div class="no-genres" style="padding: var(--space-4); color: var(--text-muted); text-align: center;">No more genres available</div>';
    genreSuggestions.classList.add("show");
    console.log("No available genres");
    return;
  }
  
  // Display genres as tags/chips in a grid
  genreSuggestions.innerHTML = `
    <div class="genre-tags-container">
      ${availableGenres.map(genre => `
        <div class="genre-tag" data-genre-id="${genre.genreId}" data-genre-name="${genre.genreName}">
          ${genre.genreName}
        </div>
      `).join('')}
    </div>
  `;
  
  console.log("Genre suggestions HTML created, adding event listeners");
  
  // Add click event listeners to each genre tag
  genreSuggestions.querySelectorAll('.genre-tag').forEach(item => {
    item.addEventListener('click', () => {
      const genreId = parseInt(item.dataset.genreId);
      const genreName = item.dataset.genreName;
      console.log("Genre clicked:", genreId, genreName);
      selectGenre(genreId, genreName);
    });
  });
  
  genreSuggestions.classList.add("show");
}

function selectGenre(genreId, genreName) {
  console.log("selectGenre called with:", genreId, genreName);
  // Add to selected genres
  if (!selectedGenres.some(g => g.genreId === genreId)) {
    selectedGenres.push({ genreId, genreName });
    updateSelectedGenresDisplay();
  }
  
  // Clear input
  genreInput.value = '';
  genreSuggestions.classList.remove("show");
}

function removeGenre(genreId) {
  selectedGenres = selectedGenres.filter(g => g.genreId !== genreId);
  updateSelectedGenresDisplay();
}

function updateSelectedGenresDisplay() {
  if (selectedGenres.length === 0) {
    selectedGenresDiv.innerHTML = '';
    return;
  }
  
  selectedGenresDiv.innerHTML = selectedGenres
    .map(genre => `
      <span class="selected-item">
        ${genre.genreName}
        <button type="button" onclick="removeGenre(${genre.genreId})" class="remove-item">×</button>
      </span>
    `)
    .join('');
}

const uploadForm = document.getElementById("uploadForm");
if (uploadForm) {
  uploadForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const formData = new FormData(uploadForm);
    
    // Add selected genres as JSON
    console.log("Selected genres before upload:", selectedGenres);
    if (selectedGenres.length > 0) {
      const genreIds = selectedGenres.map(g => g.genreId);
      console.log("Genre IDs:", genreIds);
      const genreIdsJson = JSON.stringify(genreIds);
      console.log("Genre IDs JSON:", genreIdsJson);
      formData.append("genreIds", genreIdsJson);
    } else {
      console.log("No genres selected!");
    }
    
    // Log all FormData entries
    console.log("FormData entries:");
    for (let [key, value] of formData.entries()) {
      console.log(key, ":", value);
    }
    
    const progressBar = document.getElementById("progressBar");
    const progressFill = document.getElementById("progressFill");
    const messageDiv = document.getElementById("message");

    progressBar.style.display = "block";
    messageDiv.style.display = "none";

    try {
      const xhr = new XMLHttpRequest();

      // Track upload progress
      xhr.upload.addEventListener("progress", (e) => {
        if (e.lengthComputable) {
          const percentComplete = (e.loaded / e.total) * 100;
          progressFill.style.width = percentComplete + "%";
        }
      });

      // Handle completion
      xhr.addEventListener("load", () => {
        if (xhr.status === 201 || xhr.status === 200) {
          messageDiv.className = "message success-message";
          messageDiv.textContent = "✅ Book uploaded successfully! Check console logs.";
          messageDiv.style.display = "block";
          uploadForm.reset();
          progressBar.style.display = "none";

          console.log("Upload successful!");
          // Redirect removed for debugging
        } else {
          const response = JSON.parse(xhr.responseText);
          throw new Error(response.error || "Upload failed");
        }
      });

      // Handle errors
      xhr.addEventListener("error", () => {
        throw new Error("Network error");
      });

      // Send request
      xhr.open("POST", `${API_URL}/books`);
      xhr.setRequestHeader(
        "Authorization",
        `Bearer ${localStorage.getItem("token")}`
      );
      xhr.send(formData);
    } catch (error) {
      console.error("Upload error:", error);
      messageDiv.className = "message error-message";
      messageDiv.textContent = "❌ Upload failed: " + error.message;
      messageDiv.style.display = "block";
      progressBar.style.display = "none";
    }
  });
}
