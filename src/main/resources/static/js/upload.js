const API_URL = "http://localhost:8080/api";

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

if (genreInput) {
  genreInput.addEventListener("input", (e) => {
    clearTimeout(genreDebounceTimer);
    const query = e.target.value.trim();
    
    if (query.length < 1) {
      genreSuggestions.classList.remove("show");
      return;
    }
    
    genreDebounceTimer = setTimeout(async () => {
      try {
        const response = await fetch(`${API_URL}/genres/search?q=${encodeURIComponent(query)}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        });
        
        if (response.ok) {
          const genres = await response.json();
          displayGenreSuggestions(genres);
        }
      } catch (error) {
        console.error("Error fetching genres:", error);
      }
    }, 300);
  });
  
  // Close suggestions when clicking outside
  document.addEventListener("click", (e) => {
    if (e.target !== genreInput && e.target !== genreSuggestions) {
      genreSuggestions.classList.remove("show");
    }
  });
}

function displayGenreSuggestions(genres) {
  // Filter out already selected genres
  const availableGenres = genres.filter(g => 
    !selectedGenres.some(sg => sg.genreId === g.genreId)
  );
  
  if (availableGenres.length === 0) {
    genreSuggestions.classList.remove("show");
    return;
  }
  
  genreSuggestions.innerHTML = availableGenres
    .map(genre => `
      <div class="suggestion-item" onclick="selectGenre(${genre.genreId}, '${genre.genreName.replace(/'/g, "\\'")}')">
        ${genre.genreName}
      </div>
    `)
    .join('');
  
  genreSuggestions.classList.add("show");
}

function selectGenre(genreId, genreName) {
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
    if (selectedGenres.length > 0) {
      formData.append("genreIds", JSON.stringify(selectedGenres.map(g => g.genreId)));
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
          messageDiv.textContent = "✅ Book uploaded successfully!";
          messageDiv.style.display = "block";
          uploadForm.reset();
          progressBar.style.display = "none";

          setTimeout(() => {
            window.location.href = "/library.html";
          }, 2000);
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
