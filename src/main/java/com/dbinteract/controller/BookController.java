package com.dbinteract.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dbinteract.dao.BookDAO;
import com.dbinteract.dao.UserBookDAO;
import com.dbinteract.models.Book;
import com.dbinteract.security.UserPrincipal;
import com.dbinteract.service.FileStorageService;

@RestController
@RequestMapping("/api/books")
public class BookController {
    
    private final BookDAO bookDAO;
    private final UserBookDAO userBookDAO;
    private final FileStorageService fileStorageService;
    
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "epub");
    
    public BookController(BookDAO bookDAO, UserBookDAO userBookDAO, FileStorageService fileStorageService) {
        this.bookDAO = bookDAO;
        this.userBookDAO = userBookDAO;
        this.fileStorageService = fileStorageService;
    }
    
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        try {
            List<Book> books = bookDAO.getAllBooks();
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable int id) {
        try {
            Book book = bookDAO.getBookById(id);
            if (book != null) {
                return ResponseEntity.ok(book);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{id}/details")
    public ResponseEntity<?> getBookDetails(@PathVariable int id) {
        try {
            Map<String, Object> details = bookDAO.getBookDetails(id);
            if (!details.isEmpty()) {
                return ResponseEntity.ok(details);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Book>> searchBooks(@RequestParam String q) {
        try {
            List<Book> books = bookDAO.searchBooks(q);
            return ResponseEntity.ok(books);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/read/{id}")
    public ResponseEntity<Resource> readBook(@PathVariable int id) {
        try {
            Book book = bookDAO.getBookById(id);
            if (book == null) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(book.getFilePath());
            if (!Files.exists(filePath)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            
            String filename = filePath.getFileName().toString();
            Resource resource = fileStorageService.loadFileAsResource(filename);
            
            String contentType = getMimeType(filename);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .header("X-Highlights-URL", "/api/books/" + id + "/highlights")
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadBook(
            @RequestParam("title") String title,
            @RequestParam(value = "author", required = false) String author,
            @RequestParam(value = "published_date", required = false) String publishedDate,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam("ebookContent") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            // Validate user authentication
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int uploaderId = userPrincipal.getUserId();
            
            // Validate title
            if (title == null || title.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Book title is missing or invalid");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Validate file
            if (file == null || file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Ebook file is missing or invalid");
                return ResponseEntity.badRequest().body(error);
            }
            
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename).toLowerCase();
            
            // Validate file extension
            if (extension.isEmpty() || !ALLOWED_EXTENSIONS.contains(extension)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid file type. Allowed types are: " + ALLOWED_EXTENSIONS);
                return ResponseEntity.badRequest().body(error);
            }
            
            // Store the file
            String storedFilename = fileStorageService.storeFile(file);
            Path uploadPath = Paths.get(fileStorageService.getUploadDir()).toAbsolutePath().normalize();
            String filePath = uploadPath.resolve(storedFilename).toString();
            
            // Determine format
            String format = extension.equalsIgnoreCase("pdf") ? "PDF" : "EPUB";
            
            // Create new book
            Book newBook = new Book();
            newBook.setName(title);
            newBook.setLanguage(language != null && !language.trim().isEmpty() ? language : "Unknown");
            newBook.setFormat(format);
            newBook.setFilePath(filePath);
            newBook.setUserId(uploaderId);
            newBook.setPublisherId(null);
            
            // Save to database
            bookDAO.addBook(newBook, uploaderId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(newBook);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Could not upload book: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBook(
            @PathVariable int id,
            @RequestBody Book updatedBook,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int userId = userPrincipal.getUserId();
            
            // Check if book exists
            Book existingBook = bookDAO.getBookById(id);
            if (existingBook == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Book not found");
                return ResponseEntity.notFound().build();
            }
            
            // Check ownership
            if (existingBook.getUserId() != userId) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Forbidden: You do not have permission to update this book");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            
            // Update book (preserve bookId and userId)
            updatedBook.setBookId(id);
            updatedBook.setUserId(userId);
            
            boolean success = bookDAO.updateBook(updatedBook, userId);
            
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Book updated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to update book");
                return ResponseEntity.internalServerError().body(error);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid request data: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBook(
            @PathVariable int id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int userId = userPrincipal.getUserId();
            
            // Check if book exists
            Book book = bookDAO.getBookById(id);
            if (book == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Book not found");
                return ResponseEntity.notFound().build();
            }
            
            // Check ownership
            if (book.getUserId() != userId) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Forbidden: You do not have permission to delete this book");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            
            // Delete file
            String filePath = book.getFilePath();
            Path path = Paths.get(filePath);
            String filename = path.getFileName().toString();
            
            // Delete from database
            boolean success = bookDAO.deleteBook(id, userId);
            
            if (success) {
                // Delete file from storage
                fileStorageService.deleteFile(filename);
                
                Map<String, String> response = new HashMap<>();
                response.put("message", "Book deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to delete book");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "A server error occurred during deletion: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * GET /api/books/{id}/rating - Get average rating for a book
     */
    @GetMapping("/{id}/rating")
    public ResponseEntity<?> getBookRating(@PathVariable int id) {
        try {
            Map<String, Object> rating = userBookDAO.getAverageRating(id);
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get rating: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    private String getMimeType(String filename) {
        String lowercased = filename.toLowerCase();
        if (lowercased.endsWith(".pdf")) {
            return "application/pdf";
        }
        if (lowercased.endsWith(".epub")) {
            return "application/epub+zip";
        }
        return "application/octet-stream";
    }
    
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1);
    }
}
