package com.dbinteract.dao;

import com.dbinteract.database.ConnectionManager;
import com.dbinteract.models.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for AUTHOR table
 */
public class AuthorDAO {
    
    /**
     * Get all authors
     */
    public List<Author> findAll() throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM AUTHOR ORDER BY AuthorName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Author author = new Author();
                author.setAuthorId(rs.getInt("AuthorId"));
                author.setAuthorName(rs.getString("AuthorName"));
                authors.add(author);
            }
        }
        return authors;
    }
}
