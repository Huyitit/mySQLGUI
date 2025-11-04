package com.library.dao;

import com.library.database.ConnectionManager;
import com.library.models.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for GENRE table
 */
public class GenreDAO {
    
    /**
     * Get all genres
     */
    public List<Genre> findAll() throws SQLException {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM GENRE ORDER BY GenreName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Genre genre = new Genre();
                genre.setGenreId(rs.getInt("GenreId"));
                genre.setGenreName(rs.getString("GenreName"));
                genres.add(genre);
            }
        }
        return genres;
    }
    
    /**
     * Find genre by ID
     */
    public Genre findById(int genreId) throws SQLException {
        String sql = "SELECT * FROM GENRE WHERE GenreId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, genreId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Genre genre = new Genre();
                    genre.setGenreId(rs.getInt("GenreId"));
                    genre.setGenreName(rs.getString("GenreName"));
                    return genre;
                }
            }
        }
        return null;
    }
    
    /**
     * Insert new genre
     */
    public int insert(Genre genre) throws SQLException {
        String sql = "INSERT INTO GENRE (GenreName) VALUES (?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, genre.getGenreName());
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
     * Update existing genre
     */
    public boolean update(Genre genre) throws SQLException {
        String sql = "UPDATE GENRE SET GenreName = ? WHERE GenreId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, genre.getGenreName());
            stmt.setInt(2, genre.getGenreId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete genre by ID
     */
    public boolean delete(int genreId) throws SQLException {
        String sql = "DELETE FROM GENRE WHERE GenreId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, genreId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Search genres by name
     */
    public List<Genre> searchByName(String keyword) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM GENRE WHERE GenreName LIKE ? ORDER BY GenreName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + keyword + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Genre genre = new Genre();
                    genre.setGenreId(rs.getInt("GenreId"));
                    genre.setGenreName(rs.getString("GenreName"));
                    genres.add(genre);
                }
            }
        }
        return genres;
    }
}
