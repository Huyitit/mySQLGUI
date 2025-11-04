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
import com.dbinteract.models.Author;

/**
 * Data Access Object for AUTHOR table
 */
@Repository
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
    
    /**
     * Search authors by name (for autocomplete)
     */
    public List<Author> searchByName(String query) throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM AUTHOR WHERE AuthorName LIKE ? ORDER BY AuthorName LIMIT 10";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + query + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Author author = new Author();
                    author.setAuthorId(rs.getInt("AuthorId"));
                    author.setAuthorName(rs.getString("AuthorName"));
                    authors.add(author);
                }
            }
        }
        return authors;
    }
}
