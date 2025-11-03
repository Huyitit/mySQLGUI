package com.dbinteract.dao;

import com.dbinteract.database.ConnectionManager;
import com.dbinteract.models.Genre;

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
}
