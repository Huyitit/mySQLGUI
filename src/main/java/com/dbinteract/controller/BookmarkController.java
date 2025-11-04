package com.dbinteract.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dbinteract.dao.BookmarkDAO;
import com.dbinteract.security.UserPrincipal;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    
    private final BookmarkDAO bookmarkDAO;
    
    public BookmarkController(BookmarkDAO bookmarkDAO) {
        this.bookmarkDAO = bookmarkDAO;
    }
    
    /**
     * GET /api/bookmarks?bookId={id} - Get user's bookmarks for a specific book
     */
    @GetMapping
    public ResponseEntity<?> getBookmarks(
            @RequestParam int bookId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int userId = userPrincipal.getUserId();
            List<Map<String, Object>> bookmarks = bookmarkDAO.getBookmarksByUserAndBook(userId, bookId);
            
            return ResponseEntity.ok(bookmarks);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to load bookmarks: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * POST /api/bookmarks - Add a new bookmark
     */
    @PostMapping
    public ResponseEntity<?> addBookmark(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        
        try {
            if (userPrincipal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Unauthorized user");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            
            int userId = userPrincipal.getUserId();
            int bookId = Integer.parseInt(body.get("bookId").toString());
            String bookmarkName = body.get("bookmarkName").toString();
            String location = body.get("location").toString();
            
            boolean success = bookmarkDAO.addBookmark(userId, bookId, bookmarkName, location);
            
            if (success) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Bookmark added successfully");
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Failed to add bookmark");
                return ResponseEntity.internalServerError().body(error);
            }
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add bookmark: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
