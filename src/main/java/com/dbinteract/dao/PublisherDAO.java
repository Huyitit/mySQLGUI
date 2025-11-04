package com.dbinteract.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.dbinteract.database.ConnectionManager;
import com.dbinteract.models.Publisher;

/**
 * Data Access Object for PUBLISHER table
 */
@Repository
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
     * Search publishers by name (for autocomplete)
     */
    public List<Publisher> searchByName(String query) throws SQLException {
        List<Publisher> publishers = new ArrayList<>();
        String sql = "SELECT * FROM PUBLISHER WHERE PublisherName LIKE ? ORDER BY PublisherName LIMIT 10";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + query + "%");
            
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
