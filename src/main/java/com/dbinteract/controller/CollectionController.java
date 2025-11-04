package com.dbinteract.controller;

import java.sql.SQLException;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbinteract.dao.CollectionDAO;
import com.dbinteract.models.Collection;
import com.dbinteract.security.UserPrincipal;

@RestController
@RequestMapping("/api/collections")
public class CollectionController {
    
    private final CollectionDAO collectionDAO;
    
    public CollectionController(CollectionDAO collectionDAO) {
        this.collectionDAO = collectionDAO;
    }
    
    /**
     * GET /api/collections - Get all user's collections with book counts
     */
    @GetMapping
    public ResponseEntity<?> getUserCollections(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int userId = userPrincipal.getUserId();
            List<Map<String, Object>> collections = collectionDAO.getCollectionsWithBookCount(userId);
            
            return ResponseEntity.ok(collections);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load collections: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * POST /api/collections - Create a new collection
     */
    @PostMapping
    public ResponseEntity<?> createCollection(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            String collectionName = body.get("collectionName");
            if (collectionName == null || collectionName.trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Collection name is required");
                return ResponseEntity.badRequest().body(error);
            }
            
            int userId = userPrincipal.getUserId();
            Collection collection = collectionDAO.createCollection(collectionName, userId);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(collection);
            
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create collection: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * GET /api/collections/{id}/books - Get books in a collection
     */
    @GetMapping("/{id}/books")
    public ResponseEntity<?> getBooksInCollection(@PathVariable int id) {
        try {
            List<Map<String, Object>> books = collectionDAO.getBooksInCollection(id);
            return ResponseEntity.ok(books);
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load books: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * POST /api/collections/{id}/books/{bookId} - Add book to collection
     */
    @PostMapping("/{id}/books/{bookId}")
    public ResponseEntity<?> addBookToCollection(
            @PathVariable int id,
            @PathVariable int bookId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            collectionDAO.addBookToCollection(id, bookId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Book added to collection successfully");
            return ResponseEntity.ok(response);
            
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add book to collection: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * DELETE /api/collections/{id}/books/{bookId} - Remove book from collection
     */
    @DeleteMapping("/{id}/books/{bookId}")
    public ResponseEntity<?> removeBookFromCollection(
            @PathVariable int id,
            @PathVariable int bookId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            collectionDAO.removeBookFromCollection(id, bookId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Book removed from collection successfully");
            return ResponseEntity.ok(response);
            
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to remove book from collection: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * DELETE /api/collections/{id} - Delete collection
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollection(
            @PathVariable int id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int userId = userPrincipal.getUserId();
            collectionDAO.deleteCollection(id, userId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Collection deleted successfully");
            return ResponseEntity.ok(response);
            
        } catch (SQLException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete collection: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
