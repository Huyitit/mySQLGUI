package com.dbinteract.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.dbinteract.database.ConnectionManager;
import com.dbinteract.models.UserBook;

/**
 * Data Access Object for USERBOOK table and complex queries
 */
@Repository
public class UserBookDAO {
    
    /**
     * QUERY 1: Get user's library with book details, authors, progress, and ratings
     * Returns list of maps containing all book information for a user
     */
    public List<Map<String, Object>> getUserLibraryWithDetails(String username) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String sql = "SELECT " +
                "    b.BookId, " +
                "    b.Name AS BookTitle, " +
                "    b.Language, " +
                "    b.Format, " +
                "    GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors, " +
                "    ub.Progress, " +
                "    ub.UserRating, " +
                "    ub.LastReadDate " +
                "FROM USERBOOK ub " +
                "JOIN BOOK b ON ub.BookId = b.BookId " +
                "JOIN USER u ON ub.UserId = u.UserId " +
                "LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId " +
                "LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId " +
                "WHERE u.UserName = ? " +
                "GROUP BY b.BookId, b.Name, b.Language, b.Format, ub.Progress, ub.UserRating, ub.LastReadDate " +
                "ORDER BY ub.LastReadDate DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("bookId", rs.getInt("BookId"));
                    row.put("name", rs.getString("BookTitle"));
                    row.put("language", rs.getString("Language"));
                    row.put("format", rs.getString("Format"));
                    row.put("authors", rs.getString("Authors"));
                    row.put("progress", rs.getString("Progress"));
                    
                    int rating = rs.getInt("UserRating");
                    row.put("userRating", rs.wasNull() ? null : rating);
                    
                    Timestamp lastRead = rs.getTimestamp("LastReadDate");
                    row.put("lastReadDate", lastRead != null ? lastRead.toString() : "Never");
                    
                    results.add(row);
                }
            }
        }
        return results;
    }
    
    /**
     * QUERY 5: Get average rating for a book
     */
    public Map<String, Object> getBookRating(String bookTitle) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        String sql = "SELECT " +
                "    AVG(ub.UserRating) AS AverageRating, " +
                "    COUNT(ub.UserRating) AS TotalRatings " +
                "FROM USERBOOK ub " +
                "JOIN BOOK b ON ub.BookId = b.BookId " +
                "WHERE b.Name = ? AND ub.UserRating IS NOT NULL";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bookTitle);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avgRating = rs.getDouble("AverageRating");
                    result.put("BookTitle", bookTitle);
                    result.put("AverageRating", rs.wasNull() ? 0.0 : avgRating);
                    result.put("TotalRatings", rs.getInt("TotalRatings"));
                }
            }
        }
        return result;
    }
    
    /**
     * Get average rating for a book by ID
     */
    public Map<String, Object> getAverageRating(int bookId) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        String sql = "SELECT " +
                "    AVG(ub.UserRating) AS AverageRating, " +
                "    COUNT(ub.UserRating) AS TotalRatings " +
                "FROM USERBOOK ub " +
                "WHERE ub.BookId = ? AND ub.UserRating IS NOT NULL";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avgRating = rs.getDouble("AverageRating");
                    result.put("AverageRating", rs.wasNull() ? 0.0 : avgRating);
                    result.put("TotalRatings", rs.getInt("TotalRatings"));
                } else {
                    result.put("AverageRating", 0.0);
                    result.put("TotalRatings", 0);
                }
            }
        }
        return result;
    }
    
    /**
     * QUERY 6: Get top N most popular books (most added to libraries)
     */
    public List<Map<String, Object>> getTopPopularBooks(int limit) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String sql = "SELECT " +
                "    b.Name AS BookTitle, " +
                "    GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors, " +
                "    COUNT(DISTINCT ub.UserId) AS TotalUsers, " +
                "    AVG(ub.UserRating) AS AverageRating " +
                "FROM USERBOOK ub " +
                "JOIN BOOK b ON ub.BookId = b.BookId " +
                "LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId " +
                "LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId " +
                "GROUP BY b.BookId, b.Name " +
                "ORDER BY TotalUsers DESC " +
                "LIMIT ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int rank = 1;
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("Rank", rank++);
                    row.put("BookTitle", rs.getString("BookTitle"));
                    row.put("Authors", rs.getString("Authors"));
                    row.put("TotalUsers", rs.getInt("TotalUsers"));
                    
                    double avgRating = rs.getDouble("AverageRating");
                    row.put("AverageRating", rs.wasNull() ? 0.0 : String.format("%.1f", avgRating));
                    
                    results.add(row);
                }
            }
        }
        return results;
    }
    
    /**
     * Add book to user's library
     * If book already exists in library, do nothing (no error)
     */
    public void addUserBook(UserBook userBook) throws SQLException {
        // First check if book already exists in user's library
        String checkSql = "SELECT COUNT(*) FROM USERBOOK WHERE UserId = ? AND BookId = ?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setInt(1, userBook.getUserId());
            checkStmt.setInt(2, userBook.getBookId());
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Book already in library, don't insert again
                    return;
                }
            }
        }
        
        // Book not in library, insert it
        String sql = "INSERT INTO USERBOOK (UserId, BookId, Progress, AddedDate, UserRating) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userBook.getUserId());
            stmt.setInt(2, userBook.getBookId());
            stmt.setString(3, userBook.getProgress());
            stmt.setTimestamp(4, userBook.getAddedDate() != null ? 
                Timestamp.valueOf(userBook.getAddedDate()) : new Timestamp(System.currentTimeMillis()));
            
            if (userBook.getUserRating() != null) {
                stmt.setInt(5, userBook.getUserRating());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Update reading progress
     */
    public void updateProgress(int userId, int bookId, String progress) throws SQLException {
        String sql = "UPDATE USERBOOK SET Progress = ?, LastReadDate = ? " +
                     "WHERE UserId = ? AND BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, progress);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, userId);
            stmt.setInt(4, bookId);
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Update user's rating for a book
     */
    public void updateRating(int userId, int bookId, int rating) throws SQLException {
        String sql = "UPDATE USERBOOK SET UserRating = ? WHERE UserId = ? AND BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rating);
            stmt.setInt(2, userId);
            stmt.setInt(3, bookId);
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Remove book from user's library
     */
    public void removeFromLibrary(int userId, int bookId) throws SQLException {
        String sql = "DELETE FROM USERBOOK WHERE UserId = ? AND BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            
            stmt.executeUpdate();
        }
    }
}
