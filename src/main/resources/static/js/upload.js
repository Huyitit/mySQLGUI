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

const uploadForm = document.getElementById("uploadForm");
if (uploadForm) {
  uploadForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const formData = new FormData(uploadForm);
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
