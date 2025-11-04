package com.library.dao;

import com.library.database.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for USERBOOK table and complex queries
 */
public class UserBookDAO {
    
    /**
     * QUERY 1: Get user's library with book details, authors, progress, and ratings
     * Returns list of maps containing all book information for a user
     */
    public List<Map<String, Object>> getUserLibraryWithDetails(String username) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String sql = "SELECT " +
                "    b.Name AS BookTitle, " +
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
                "GROUP BY b.BookId, b.Name, ub.Progress, ub.UserRating, ub.LastReadDate " +
                "ORDER BY ub.LastReadDate DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("BookTitle", rs.getString("BookTitle"));
                    row.put("Authors", rs.getString("Authors"));
                    row.put("Progress", rs.getString("Progress"));
                    
                    int rating = rs.getInt("UserRating");
                    row.put("UserRating", rs.wasNull() ? null : rating);
                    
                    Timestamp lastRead = rs.getTimestamp("LastReadDate");
                    row.put("LastReadDate", lastRead != null ? lastRead.toString() : "Never");
                    
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
}
