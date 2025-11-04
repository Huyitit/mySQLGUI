package com.library.dao;

import com.library.database.ConnectionManager;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object for generating statistics and reports
 */
public class StatisticsDAO {
    
    /**
     * Get total count of books
     */
    public int getTotalBooks() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM BOOK";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
    
    /**
     * Get total count of users
     */
    public int getTotalUsers() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM USER";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
    
    /**
     * Get total count of authors
     */
    public int getTotalAuthors() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM AUTHOR";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
    
    /**
     * Get total count of genres
     */
    public int getTotalGenres() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM GENRE";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
    
    /**
     * Get total count of publishers
     */
    public int getTotalPublishers() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM PUBLISHER";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
    
    /**
     * Get total count of collections
     */
    public int getTotalCollections() throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM COLLECTION";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }
    
    /**
     * Get books count by format
     */
    public Map<String, Integer> getBooksByFormat() throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT Format, COUNT(*) as count FROM BOOK GROUP BY Format ORDER BY count DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                result.put(rs.getString("Format"), rs.getInt("count"));
            }
        }
        return result;
    }
    
    /**
     * Get books count by language
     */
    public Map<String, Integer> getBooksByLanguage() throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT Language, COUNT(*) as count FROM BOOK GROUP BY Language ORDER BY count DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                result.put(rs.getString("Language"), rs.getInt("count"));
            }
        }
        return result;
    }
    
    /**
     * Get top N genres by book count
     */
    public Map<String, Integer> getTopGenres(int limit) throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT g.GenreName, COUNT(*) as count " +
                    "FROM GENRE g " +
                    "JOIN GENREBOOK gb ON g.GenreId = gb.GenreId " +
                    "GROUP BY g.GenreId, g.GenreName " +
                    "ORDER BY count DESC " +
                    "LIMIT ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("GenreName"), rs.getInt("count"));
                }
            }
        }
        return result;
    }
    
    /**
     * Get top N authors by book count
     */
    public Map<String, Integer> getTopAuthors(int limit) throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT a.AuthorName, COUNT(*) as count " +
                    "FROM AUTHOR a " +
                    "JOIN AUTHORBOOK ab ON a.AuthorId = ab.AuthorId " +
                    "GROUP BY a.AuthorId, a.AuthorName " +
                    "ORDER BY count DESC " +
                    "LIMIT ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("AuthorName"), rs.getInt("count"));
                }
            }
        }
        return result;
    }
    
    /**
     * Get top N publishers by book count
     */
    public Map<String, Integer> getTopPublishers(int limit) throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT p.PublisherName, COUNT(*) as count " +
                    "FROM PUBLISHER p " +
                    "JOIN BOOK b ON p.PublisherId = b.PublisherId " +
                    "GROUP BY p.PublisherId, p.PublisherName " +
                    "ORDER BY count DESC " +
                    "LIMIT ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("PublisherName"), rs.getInt("count"));
                }
            }
        }
        return result;
    }
    
    /**
     * Get books count by user
     */
    public Map<String, Integer> getBooksByUser() throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT u.UserName, COUNT(*) as count " +
                    "FROM USER u " +
                    "LEFT JOIN BOOK b ON u.UserId = b.UserId " +
                    "GROUP BY u.UserId, u.UserName " +
                    "ORDER BY count DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                result.put(rs.getString("UserName"), rs.getInt("count"));
            }
        }
        return result;
    }
    
    /**
     * Get collections count by user
     */
    public Map<String, Integer> getCollectionsByUser() throws SQLException {
        Map<String, Integer> result = new HashMap<>();
        String sql = "SELECT u.UserName, COUNT(*) as count " +
                    "FROM USER u " +
                    "LEFT JOIN COLLECTION c ON u.UserId = c.UserId " +
                    "GROUP BY u.UserId, u.UserName " +
                    "ORDER BY count DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                result.put(rs.getString("UserName"), rs.getInt("count"));
            }
        }
        return result;
    }
    
    /**
     * Get all statistics in one call
     */
    public Map<String, Object> getAllStatistics() throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalBooks", getTotalBooks());
        stats.put("totalUsers", getTotalUsers());
        stats.put("totalAuthors", getTotalAuthors());
        stats.put("totalGenres", getTotalGenres());
        stats.put("totalPublishers", getTotalPublishers());
        stats.put("totalCollections", getTotalCollections());
        stats.put("booksByFormat", getBooksByFormat());
        stats.put("booksByLanguage", getBooksByLanguage());
        stats.put("topGenres", getTopGenres(5));
        stats.put("topAuthors", getTopAuthors(5));
        stats.put("topPublishers", getTopPublishers(5));
        
        return stats;
    }
}
