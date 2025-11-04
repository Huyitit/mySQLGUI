package com.dbinteract.controller;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbinteract.dao.UserBookDAO;
import com.dbinteract.models.UserBook;
import com.dbinteract.security.UserPrincipal;

@RestController
@RequestMapping("/api/library")
public class UserBookController {
    
    private final UserBookDAO userBookDAO;
    
    public UserBookController(UserBookDAO userBookDAO) {
        this.userBookDAO = userBookDAO;
    }
    
    /**
     * GET /api/library - Get user's library
     */
    @GetMapping
    public ResponseEntity<?> getUserLibrary(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            String username = userPrincipal.getUsername();
            List<Map<String, Object>> library = userBookDAO.getUserLibraryWithDetails(username);
            
            return ResponseEntity.ok(library);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load library: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * POST /api/library/{bookId} - Add book to user's library
     */
    @PostMapping("/{bookId}")
    public ResponseEntity<?> addBookToLibrary(
            @PathVariable int bookId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int userId = userPrincipal.getUserId();
            
            // Create new UserBook entry
            UserBook userBook = new UserBook();
            userBook.setUserId(userId);
            userBook.setBookId(bookId);
            userBook.setProgress("0");
            userBook.setAddedDate(LocalDateTime.now());
            userBook.setUserRating(null);
            
            userBookDAO.addUserBook(userBook);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Book added to library successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add book to library: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * PUT /api/library/{bookId}/progress - Update reading progress
     */
    @PutMapping("/{bookId}/progress")
    public ResponseEntity<?> updateProgress(
            @PathVariable int bookId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int userId = userPrincipal.getUserId();
            String progress = body.get("progress").toString();
            
            userBookDAO.updateProgress(userId, bookId, progress);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Progress updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update progress: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * PUT /api/library/{bookId}/rating - Rate a book
     */
    @PutMapping("/{bookId}/rating")
    public ResponseEntity<?> rateBook(
            @PathVariable int bookId,
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int userId = userPrincipal.getUserId();
            int rating = Integer.parseInt(body.get("rating").toString());
            
            // Validate rating is between 1-5
            if (rating < 1 || rating > 5) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Rating must be between 1 and 5");
                return ResponseEntity.badRequest().body(error);
            }
            
            userBookDAO.updateRating(userId, bookId, rating);
            
            // Get updated average rating
            Map<String, Object> avgRating = userBookDAO.getAverageRating(bookId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Rating submitted successfully");
            response.put("userRating", rating);
            response.put("averageRating", avgRating.get("AverageRating"));
            response.put("totalRatings", avgRating.get("TotalRatings"));
            return ResponseEntity.ok(response);
            
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to submit rating: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid rating value");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * DELETE /api/library/{bookId} - Remove book from user's library
     */
    @DeleteMapping("/{bookId}")
    public ResponseEntity<?> removeBookFromLibrary(
            @PathVariable int bookId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int userId = userPrincipal.getUserId();
            
            userBookDAO.removeFromLibrary(userId, bookId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Book removed from library successfully");
            return ResponseEntity.ok(response);
            
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to remove book: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
