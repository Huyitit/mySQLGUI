package com.library.dao;

import com.library.database.ConnectionManager;
import com.library.models.Collection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for COLLECTION table
 */
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
     * Find all collections
     */
    public List<Collection> findAll() throws SQLException {
        List<Collection> collections = new ArrayList<>();
        String sql = "SELECT * FROM COLLECTION ORDER BY CollectionName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
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
        return collections;
    }
    
    /**
     * Find collection by ID
     */
    public Collection findById(int collectionId) throws SQLException {
        String sql = "SELECT * FROM COLLECTION WHERE CollectionId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Collection collection = new Collection();
                    collection.setCollectionId(rs.getInt("CollectionId"));
                    collection.setCollectionName(rs.getString("CollectionName"));
                    collection.setUserId(rs.getInt("UserId"));
                    
                    Timestamp created = rs.getTimestamp("CreatedDate");
                    if (created != null) {
                        collection.setCreatedDate(created.toLocalDateTime());
                    }
                    
                    return collection;
                }
            }
        }
        return null;
    }
    
    /**
     * Insert new collection
     */
    public int insert(Collection collection) throws SQLException {
        String sql = "INSERT INTO COLLECTION (CollectionName, UserId) VALUES (?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, collection.getCollectionName());
            stmt.setInt(2, collection.getUserId());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    /**
     * Update existing collection
     */
    public boolean update(Collection collection) throws SQLException {
        String sql = "UPDATE COLLECTION SET CollectionName = ? WHERE CollectionId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, collection.getCollectionName());
            stmt.setInt(2, collection.getCollectionId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete collection by ID
     */
    public boolean delete(int collectionId) throws SQLException {
        String sql = "DELETE FROM COLLECTION WHERE CollectionId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            return stmt.executeUpdate() > 0;
        }
    }
}
