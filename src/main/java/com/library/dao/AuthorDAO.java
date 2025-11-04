package com.library.dao;

import com.library.database.ConnectionManager;
import com.library.models.Author;

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
    
    /**
     * Find author by ID
     */
    public Author findById(int authorId) throws SQLException {
        String sql = "SELECT * FROM AUTHOR WHERE AuthorId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, authorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Author author = new Author();
                    author.setAuthorId(rs.getInt("AuthorId"));
                    author.setAuthorName(rs.getString("AuthorName"));
                    return author;
                }
            }
        }
        return null;
    }
    
    /**
     * Insert new author
     */
    public int insert(Author author) throws SQLException {
        String sql = "INSERT INTO AUTHOR (AuthorName) VALUES (?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, author.getAuthorName());
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
     * Update existing author
     */
    public boolean update(Author author) throws SQLException {
        String sql = "UPDATE AUTHOR SET AuthorName = ? WHERE AuthorId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, author.getAuthorName());
            stmt.setInt(2, author.getAuthorId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete author by ID
     */
    public boolean delete(int authorId) throws SQLException {
        String sql = "DELETE FROM AUTHOR WHERE AuthorId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, authorId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Search authors by name
     */
    public List<Author> searchByName(String keyword) throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM AUTHOR WHERE AuthorName LIKE ? ORDER BY AuthorName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + keyword + "%");
            
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
