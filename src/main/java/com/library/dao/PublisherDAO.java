package com.library.dao;

import com.library.database.ConnectionManager;
import com.library.models.Publisher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for PUBLISHER table
 */
public class PublisherDAO {
    
    /**
     * Get all publishers
     */
    public List<Publisher> findAll() throws SQLException {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT * FROM PUBLISHER ORDER BY PublisherName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Publisher publisher = new Publisher();
                publisher.setPublisherId(rs.getInt("PublisherId"));
                publisher.setPublisherName(rs.getString("PublisherName"));
                publishers.add(publisher);
            }
        }
        return publishers;
    }
    
    /**
     * Find publisher by ID
     */
    public Publisher findById(int publisherId) throws SQLException {
        String sql = "SELECT * FROM PUBLISHER WHERE PublisherId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, publisherId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Publisher publisher = new Publisher();
                    publisher.setPublisherId(rs.getInt("PublisherId"));
                    publisher.setPublisherName(rs.getString("PublisherName"));
                    return publisher;
                }
            }
        }
        return null;
    }
    
    /**
     * Insert new publisher
     */
    public int insert(Publisher publisher) throws SQLException {
        String sql = "INSERT INTO PUBLISHER (PublisherName) VALUES (?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, publisher.getPublisherName());
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
     * Update existing publisher
     */
    public boolean update(Publisher publisher) throws SQLException {
        String sql = "UPDATE PUBLISHER SET PublisherName = ? WHERE PublisherId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, publisher.getPublisherName());
            stmt.setInt(2, publisher.getPublisherId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete publisher by ID
     */
    public boolean delete(int publisherId) throws SQLException {
        String sql = "DELETE FROM PUBLISHER WHERE PublisherId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, publisherId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Search publishers by name
     */
    public List<Publisher> searchByName(String keyword) throws SQLException {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT * FROM PUBLISHER WHERE PublisherName LIKE ? ORDER BY PublisherName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + keyword + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Publisher publisher = new Publisher();
                    publisher.setPublisherId(rs.getInt("PublisherId"));
                    publisher.setPublisherName(rs.getString("PublisherName"));
                    publishers.add(publisher);
                }
            }
        }
        return publishers;
    }
}
