package com.dbinteract.dao;

import com.dbinteract.database.ConnectionManager;
import com.dbinteract.models.Collection;

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
}
