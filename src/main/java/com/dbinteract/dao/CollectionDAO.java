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
import com.dbinteract.models.Collection;

/**
 * Data Access Object for COLLECTION table
 */
@Repository
public class CollectionDAO {
    
    /**
     * Get all collections for a user
     */
    public List<Collection> findByUserId(int userId) throws SQLException {
        List<Collection> collections = new ArrayList<>();
        String sql = "SELECT * FROM COLLECTION WHERE UserId = ? ORDER BY CollectionName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Collection collection = new Collection();
                    collection.setCollectionId(rs.getInt("CollectionId"));
                    collection.setCollectionName(rs.getString("CollectionName"));
                    collection.setUserId(rs.getInt("UserId"));
                    
                    Timestamp created = rs.getTimestamp("CreatedDate");
                    if (created != null) {
                        collection.setCreatedDate(created.toLocalDateTime());
                    }
                    
                    collections.add(collection);
                }
            }
        }
        return collections;
    }
    
    /**
     * Get all collections for a user by username
     */
    public List<String> findCollectionNamesByUsername(String username) throws SQLException {
        List<String> collectionNames = new ArrayList<>();
        String sql = "SELECT c.CollectionName FROM COLLECTION c " +
                    "JOIN USER u ON c.UserId = u.UserId " +
                    "WHERE u.UserName = ? ORDER BY c.CollectionName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    collectionNames.add(rs.getString("CollectionName"));
                }
            }
        }
        return collectionNames;
    }
    
    /**
     * Create a new collection
     */
    public Collection createCollection(String collectionName, int userId) throws SQLException {
        String sql = "INSERT INTO COLLECTION (CollectionName, UserId, CreatedDate) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, collectionName);
            stmt.setInt(2, userId);
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating collection failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Collection collection = new Collection();
                    collection.setCollectionId(generatedKeys.getInt(1));
                    collection.setCollectionName(collectionName);
                    collection.setUserId(userId);
                    collection.setCreatedDate(new Timestamp(System.currentTimeMillis()).toLocalDateTime());
                    return collection;
                } else {
                    throw new SQLException("Creating collection failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Get collections with book count
     */
    public List<Map<String, Object>> getCollectionsWithBookCount(int userId) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String sql = "SELECT c.CollectionId, c.CollectionName, c.CreatedDate, " +
                    "COUNT(cb.BookId) as BookCount " +
                    "FROM COLLECTION c " +
                    "LEFT JOIN COLLECTIONBOOK cb ON c.CollectionId = cb.CollectionId " +
                    "WHERE c.UserId = ? " +
                    "GROUP BY c.CollectionId, c.CollectionName, c.CreatedDate " +
                    "ORDER BY c.CreatedDate DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> collection = new HashMap<>();
                    collection.put("collectionId", rs.getInt("CollectionId"));
                    collection.put("collectionName", rs.getString("CollectionName"));
                    collection.put("createdDate", rs.getTimestamp("CreatedDate").toString());
                    collection.put("bookCount", rs.getInt("BookCount"));
                    results.add(collection);
                }
            }
        }
        return results;
    }
    
    /**
     * Add book to collection
     */
    public void addBookToCollection(int collectionId, int bookId) throws SQLException {
        String sql = "INSERT IGNORE INTO COLLECTIONBOOK (CollectionId, BookId) VALUES (?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Remove book from collection
     */
    public void removeBookFromCollection(int collectionId, int bookId) throws SQLException {
        String sql = "DELETE FROM COLLECTIONBOOK WHERE CollectionId = ? AND BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Delete collection
     */
    public void deleteCollection(int collectionId, int userId) throws SQLException {
        String sql = "DELETE FROM COLLECTION WHERE CollectionId = ? AND UserId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Get books in a collection
     */
    public List<Map<String, Object>> getBooksInCollection(int collectionId) throws SQLException {
        List<Map<String, Object>> books = new ArrayList<>();
        
        String sql = "SELECT b.BookId, b.Name, b.Language, b.Format, " +
                    "GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors " +
                    "FROM BOOK b " +
                    "JOIN COLLECTIONBOOK cb ON b.BookId = cb.BookId " +
                    "LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId " +
                    "LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId " +
                    "WHERE cb.CollectionId = ? " +
                    "GROUP BY b.BookId, b.Name, b.Language, b.Format";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> book = new HashMap<>();
                    book.put("bookId", rs.getInt("BookId"));
                    book.put("name", rs.getString("Name"));
                    book.put("language", rs.getString("Language"));
                    book.put("format", rs.getString("Format"));
                    book.put("authors", rs.getString("Authors"));
                    books.add(book);
                }
            }
        }
        return books;
    }
}
