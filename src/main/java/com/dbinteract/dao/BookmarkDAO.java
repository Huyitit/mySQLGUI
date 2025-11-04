package com.dbinteract.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.dbinteract.database.ConnectionManager;

/**
 * Data Access Object for BOOKMARK table
 */
@Repository
public class BookmarkDAO {
    
    /**
     * QUERY 3: Get all bookmarks for a specific user and book
     */
    public List<Map<String, Object>> getUserBookmarksForBook(String username, String bookTitle) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String sql = "SELECT " +
                "    bm.BookmarkName, " +
                "    bm.Location, " +
                "    bm.CreatedDate " +
                "FROM BOOKMARK bm " +
                "JOIN USERBOOK ub ON bm.UserBookId = ub.UserBookId " +
                "JOIN USER u ON ub.UserId = u.UserId " +
                "JOIN BOOK b ON ub.BookId = b.BookId " +
                "WHERE u.UserName = ? AND b.Name = ? " +
                "ORDER BY bm.CreatedDate DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, bookTitle);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("BookmarkName", rs.getString("BookmarkName"));
                    row.put("Location", rs.getString("Location"));
                    
                    Timestamp created = rs.getTimestamp("CreatedDate");
                    row.put("CreatedDate", created != null ? created.toString() : "");
                    
                    results.add(row);
                }
            }
        }
        return results;
    }
    
    /**
     * Get bookmarks by userId and bookId
     */
    public List<Map<String, Object>> getBookmarksByUserAndBook(int userId, int bookId) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String sql = "SELECT bm.BookmarkId, bm.BookmarkName, bm.Location, bm.CreatedDate " +
                     "FROM BOOKMARK bm " +
                     "JOIN USERBOOK ub ON bm.UserBookId = ub.UserBookId " +
                     "WHERE ub.UserId = ? AND ub.BookId = ? " +
                     "ORDER BY bm.CreatedDate DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("bookmarkId", rs.getInt("BookmarkId"));
                    row.put("bookmarkName", rs.getString("BookmarkName"));
                    row.put("location", rs.getString("Location"));
                    
                    Timestamp created = rs.getTimestamp("CreatedDate");
                    row.put("createdDate", created != null ? created.toString() : "");
                    
                    results.add(row);
                }
            }
        }
        return results;
    }
    
    /**
     * Add a bookmark
     */
    public boolean addBookmark(int userId, int bookId, String bookmarkName, String location) throws SQLException {
        // First get or create UserBookId
        Integer userBookId = getUserBookId(userId, bookId);
        
        if (userBookId == null) {
            // Create USERBOOK entry if it doesn't exist
            String createUserBookSql = "INSERT INTO USERBOOK (UserId, BookId, Progress, AddedDate) VALUES (?, ?, '0', ?)";
            try (Connection conn = ConnectionManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(createUserBookSql, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setInt(1, userId);
                stmt.setInt(2, bookId);
                stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userBookId = rs.getInt(1);
                    }
                }
            }
        }
        
        if (userBookId == null) {
            return false;
        }
        
        // Insert bookmark
        String sql = "INSERT INTO BOOKMARK (UserBookId, BookmarkName, Location, CreatedDate) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userBookId);
            stmt.setString(2, bookmarkName);
            stmt.setString(3, location);
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get UserBookId for a user and book
     */
    private Integer getUserBookId(int userId, int bookId) throws SQLException {
        String sql = "SELECT UserBookId FROM USERBOOK WHERE UserId = ? AND BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UserBookId");
                }
            }
        }
        return null;
    }
}
