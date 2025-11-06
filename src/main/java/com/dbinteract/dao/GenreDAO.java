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
import com.dbinteract.models.Genre;

/**
 * Data Access Object for GENRE table
 */
@Repository
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
     * Search genres by name (for autocomplete)
     */
    public List<Genre> searchByName(String query) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM GENRE WHERE GenreName LIKE ? ORDER BY GenreName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + query + "%");
            
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
