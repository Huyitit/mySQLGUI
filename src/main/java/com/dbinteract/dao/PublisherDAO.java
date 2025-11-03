package com.dbinteract.dao;

import com.dbinteract.database.ConnectionManager;
import com.dbinteract.models.Publisher;

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
}
