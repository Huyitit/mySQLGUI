const API_URL = "http://localhost:8080/api";

// Get auth headers
function getAuthHeaders() {
  return {
    Authorization: `Bearer ${localStorage.getItem("token")}`,
    "Content-Type": "application/json",
  };
}

// Check authentication
if (!localStorage.getItem("token")) {
  window.location.href = "/login.html";
}

// Logout functionality
document.getElementById("logoutBtn")?.addEventListener("click", () => {
  localStorage.removeItem("token");
  localStorage.removeItem("username");
  window.location.href = "/login.html";
});

// Load user's uploaded books
async function loadMyUploads() {
  try {
    const response = await fetch(`${API_URL}/books/my-uploads`, {
      headers: getAuthHeaders(),
    });

    if (response.ok) {
      const books = await response.json();
      displayUploads(books);
    } else {
      document.getElementById("uploadsGrid").innerHTML =
        '<div class="error-message">Failed to load your uploads</div>';
    }
  } catch (error) {
    console.error("Error loading uploads:", error);
    document.getElementById("uploadsGrid").innerHTML =
      '<div class="error-message">Error loading uploads</div>';
  }
}

// Display uploaded books
function displayUploads(books) {
  const grid = document.getElementById("uploadsGrid");

  if (books.length === 0) {
    grid.innerHTML = `
      <div class="empty-state">
        <h3>No uploads yet</h3>
        <p>You haven't uploaded any books yet.</p>
        <a href="/upload.html" class="btn btn-primary">Upload Your First Book</a>
      </div>
    `;
    return;
  }

  grid.innerHTML = books
    .map(
      (book) => `
    <div class="book-card">
      <div class="book-cover">
        <span class="book-icon">📖</span>
      </div>
      <div class="book-info">
        <h3>${book.title || "Untitled"}</h3>
        <p class="author">${book.authors || "Unknown Author"}</p>
        <p class="publisher">${book.publisher || "Unknown Publisher"}</p>
        <p class="genre">${book.genres || "No genres"}</p>
        <div class="book-meta">
          <span>📅 ${book.publishedDate || "Unknown"}</span>
          <span>🌐 ${book.language || "Unknown"}</span>
        </div>
        <div class="book-actions">
          <button class="btn btn-small" onclick="editBook(${book.bookId})">✏️ Edit</button>
          <button class="btn btn-small btn-danger" onclick="deleteBook(${book.bookId}, '${book.title?.replace(/'/g, "\\'")}')">🗑️ Delete</button>
        </div>
      </div>
    </div>
  `
    )
    .join("");
}

// Edit book
let selectedGenres = [];

async function editBook(bookId) {
  try {
    const response = await fetch(`${API_URL}/books/${bookId}/details`, {
      headers: getAuthHeaders(),
    });

    if (response.ok) {
      const book = await response.json();
      openEditModal(book);
    } else {
      alert("Failed to load book details");
    }
  } catch (error) {
    console.error("Error loading book:", error);
    alert("Error loading book details");
  }
}

function openEditModal(book) {
  document.getElementById("editBookId").value = book.bookId;
  document.getElementById("editTitle").value = book.title || "";
  document.getElementById("editAuthor").value = book.authors || "";
  document.getElementById("editPublisher").value = book.publisher || "";
  document.getElementById("editLanguage").value = book.language || "";
  document.getElementById("editPublishedDate").value = book.publishedDate || "";

  // Parse genres
  selectedGenres = [];
  if (book.genres) {
    const genreNames = book.genres.split(", ");
    // We'll need to fetch genre IDs - for now just display them
    genreNames.forEach(name => {
      selectedGenres.push({ genreName: name, genreId: null });
    });
    updateEditSelectedGenresDisplay();
  }

  document.getElementById("editModal").style.display = "block";
}

function closeEditModal() {
  document.getElementById("editModal").style.display = "none";
  selectedGenres = [];
}

// Close modal when clicking X
document.querySelector(".close")?.addEventListener("click", closeEditModal);

// Close modal when clicking outside
window.addEventListener("click", (e) => {
  const modal = document.getElementById("editModal");
  if (e.target === modal) {
    closeEditModal();
  }
});

// Handle edit form submission
document.getElementById("editBookForm")?.addEventListener("submit", async (e) => {
  e.preventDefault();

  const bookId = document.getElementById("editBookId").value;
  
  // Create FormData to send as multipart/form-data
  const formData = new FormData();
  formData.append("title", document.getElementById("editTitle").value);
  formData.append("author", document.getElementById("editAuthor").value || "");
  formData.append("publisher", document.getElementById("editPublisher").value || "");
  formData.append("language", document.getElementById("editLanguage").value || "");
  formData.append("publishedDate", document.getElementById("editPublishedDate").value || "");
  formData.append("genreIds", JSON.stringify(selectedGenres.map(g => g.genreId).filter(id => id !== null)));

  try {
    const response = await fetch(`${API_URL}/books/${bookId}`, {
      method: "PUT",
      headers: {
        "Authorization": `Bearer ${localStorage.getItem("token")}`,
        // Don't set Content-Type - let browser set it with boundary for FormData
      },
      body: formData,
    });

    if (response.ok) {
      alert("Book updated successfully!");
      closeEditModal();
      loadMyUploads();
    } else {
      const errorData = await response.json();
      alert("Failed to update book: " + (errorData.error || "Unknown error"));
    }
  } catch (error) {
    console.error("Error updating book:", error);
    alert("Error updating book: " + error.message);
  }
});

// Delete book
async function deleteBook(bookId, title) {
  if (!confirm(`Are you sure you want to delete "${title}"?`)) {
    return;
  }

  try {
    const response = await fetch(`${API_URL}/books/${bookId}`, {
      method: "DELETE",
      headers: getAuthHeaders(),
    });

    if (response.ok) {
      alert("Book deleted successfully!");
      loadMyUploads();
    } else {
      alert("Failed to delete book");
    }
  } catch (error) {
    console.error("Error deleting book:", error);
    alert("Error deleting book");
  }
}

// Genre autocomplete for edit modal
const editGenreInput = document.getElementById("editGenres");
const editGenreSuggestions = document.getElementById("editGenreSuggestions");
let editGenreDebounceTimer;

if (editGenreInput) {
  editGenreInput.addEventListener("input", (e) => {
    clearTimeout(editGenreDebounceTimer);
    const query = e.target.value.trim();

    if (query.length < 1) {
      editGenreSuggestions.classList.remove("show");
      return;
    }

    editGenreDebounceTimer = setTimeout(async () => {
      try {
        const response = await fetch(`${API_URL}/genres/search?q=${encodeURIComponent(query)}`, {
          headers: getAuthHeaders(),
        });

        if (response.ok) {
          const genres = await response.json();
          displayEditGenreSuggestions(genres);
        }
      } catch (error) {
        console.error("Error fetching genres:", error);
      }
    }, 300);
  });

  document.addEventListener("click", (e) => {
    if (e.target !== editGenreInput && e.target !== editGenreSuggestions) {
      editGenreSuggestions.classList.remove("show");
    }
  });
}

function displayEditGenreSuggestions(genres) {
  const availableGenres = genres.filter(
    (g) => !selectedGenres.some((sg) => sg.genreId === g.genreId)
  );

  if (availableGenres.length === 0) {
    editGenreSuggestions.classList.remove("show");
    return;
  }

  editGenreSuggestions.innerHTML = availableGenres
    .map(
      (genre) => `
      <div class="suggestion-item" data-genre-id="${genre.genreId}" data-genre-name="${genre.genreName}">
        ${genre.genreName}
      </div>
    `
    )
    .join("");

  editGenreSuggestions.querySelectorAll(".suggestion-item").forEach((item) => {
    item.addEventListener("click", () => {
      const genreId = parseInt(item.dataset.genreId);
      const genreName = item.dataset.genreName;
      selectEditGenre(genreId, genreName);
    });
  });

  editGenreSuggestions.classList.add("show");
}

function selectEditGenre(genreId, genreName) {
  if (!selectedGenres.some((g) => g.genreId === genreId)) {
    selectedGenres.push({ genreId, genreName });
    updateEditSelectedGenresDisplay();
  }
  editGenreInput.value = "";
  editGenreSuggestions.classList.remove("show");
}

function removeEditGenre(genreId) {
  selectedGenres = selectedGenres.filter((g) => g.genreId !== genreId);
  updateEditSelectedGenresDisplay();
}

function updateEditSelectedGenresDisplay() {
  const container = document.getElementById("editSelectedGenres");
  if (selectedGenres.length === 0) {
    container.innerHTML = "";
    return;
  }

  container.innerHTML = selectedGenres
    .map(
      (genre) => `
      <span class="selected-item">
        ${genre.genreName}
        <button type="button" onclick="removeEditGenre(${genre.genreId})" class="remove-item">×</button>
      </span>
    `
    )
    .join("");
}

// Author autocomplete for edit modal
const editAuthorInput = document.getElementById("editAuthor");
const editAuthorSuggestions = document.getElementById("editAuthorSuggestions");
let editAuthorDebounceTimer;

if (editAuthorInput) {
  editAuthorInput.addEventListener("input", (e) => {
    clearTimeout(editAuthorDebounceTimer);
    const query = e.target.value.trim();

    if (query.length < 2) {
      editAuthorSuggestions.classList.remove("show");
      return;
    }

    editAuthorDebounceTimer = setTimeout(async () => {
      try {
        const response = await fetch(`${API_URL}/authors/search?q=${encodeURIComponent(query)}`, {
          headers: getAuthHeaders(),
        });

        if (response.ok) {
          const authors = await response.json();
          displayEditAuthorSuggestions(authors);
        }
      } catch (error) {
        console.error("Error fetching authors:", error);
      }
    }, 300);
  });

  document.addEventListener("click", (e) => {
    if (e.target !== editAuthorInput && e.target !== editAuthorSuggestions) {
      editAuthorSuggestions.classList.remove("show");
    }
  });
}

function displayEditAuthorSuggestions(authors) {
  if (authors.length === 0) {
    editAuthorSuggestions.classList.remove("show");
    return;
  }

  editAuthorSuggestions.innerHTML = authors
    .map(
      (author) => `
      <div class="suggestion-item" onclick="selectEditAuthor('${author.authorName.replace(/'/g, "\\'")}')">
        ${author.authorName}
      </div>
    `
    )
    .join("");

  editAuthorSuggestions.classList.add("show");
}

function selectEditAuthor(authorName) {
  editAuthorInput.value = authorName;
  editAuthorSuggestions.classList.remove("show");
}

// Publisher autocomplete for edit modal
const editPublisherInput = document.getElementById("editPublisher");
const editPublisherSuggestions = document.getElementById("editPublisherSuggestions");
let editPublisherDebounceTimer;

if (editPublisherInput) {
  editPublisherInput.addEventListener("input", (e) => {
    clearTimeout(editPublisherDebounceTimer);
    const query = e.target.value.trim();

    if (query.length < 2) {
      editPublisherSuggestions.classList.remove("show");
      return;
    }

    editPublisherDebounceTimer = setTimeout(async () => {
      try {
        const response = await fetch(`${API_URL}/publishers/search?q=${encodeURIComponent(query)}`, {
          headers: getAuthHeaders(),
        });

        if (response.ok) {
          const publishers = await response.json();
          displayEditPublisherSuggestions(publishers);
        }
      } catch (error) {
        console.error("Error fetching publishers:", error);
      }
    }, 300);
  });

  document.addEventListener("click", (e) => {
    if (e.target !== editPublisherInput && e.target !== editPublisherSuggestions) {
      editPublisherSuggestions.classList.remove("show");
    }
  });
}

function displayEditPublisherSuggestions(publishers) {
  if (publishers.length === 0) {
    editPublisherSuggestions.classList.remove("show");
    return;
  }

  editPublisherSuggestions.innerHTML = publishers
    .map(
      (publisher) => `
      <div class="suggestion-item" onclick="selectEditPublisher('${publisher.publisherName.replace(/'/g, "\\'")}')">
        ${publisher.publisherName}
      </div>
    `
    )
    .join("");

  editPublisherSuggestions.classList.add("show");
}

function selectEditPublisher(publisherName) {
  editPublisherInput.value = publisherName;
  editPublisherSuggestions.classList.remove("show");
}

// Load uploads on page load
loadMyUploads();
