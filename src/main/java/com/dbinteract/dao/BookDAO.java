package com.dbinteract.dao;

import com.dbinteract.database.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for BOOK table and related queries
 */
public class BookDAO {
    
    /**
     * QUERY 2: Get all books by genre name
     */
    public List<Map<String, Object>> getBooksByGenre(String genreName) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String sql = "SELECT " +
                "    b.Name AS BookTitle, " +
                "    GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors, " +
                "    p.PublisherName, " +
                "    b.Language, " +
                "    b.Format " +
                "FROM BOOK b " +
                "JOIN GENREBOOK gb ON b.BookId = gb.BookId " +
                "JOIN GENRE g ON gb.GenreId = g.GenreId " +
                "LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId " +
                "LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId " +
                "LEFT JOIN PUBLISHER p ON b.PublisherId = p.PublisherId " +
                "WHERE g.GenreName = ? " +
                "GROUP BY b.BookId, b.Name, p.PublisherName, b.Language, b.Format " +
                "ORDER BY b.Name";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, genreName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("BookTitle", rs.getString("BookTitle"));
                    row.put("Authors", rs.getString("Authors"));
                    row.put("PublisherName", rs.getString("PublisherName"));
                    row.put("Language", rs.getString("Language"));
                    row.put("Format", rs.getString("Format"));
                    results.add(row);
                }
            }
        }
        return results;
    }
    
    /**
     * QUERY 4: Get all books in a collection
     */
    public List<Map<String, Object>> getBooksInCollection(String username, String collectionName) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String sql = "SELECT " +
                "    b.Name AS BookTitle, " +
                "    GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors, " +
                "    b.Language, " +
                "    b.Format " +
                "FROM COLLECTIONBOOK cb " +
                "JOIN COLLECTION c ON cb.CollectionId = c.CollectionId " +
                "JOIN BOOK b ON cb.BookId = b.BookId " +
                "JOIN USER u ON c.UserId = u.UserId " +
                "LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId " +
                "LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId " +
                "WHERE u.UserName = ? AND c.CollectionName = ? " +
                "GROUP BY b.BookId, b.Name, b.Language, b.Format " +
                "ORDER BY b.Name";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, collectionName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("BookTitle", rs.getString("BookTitle"));
                    row.put("Authors", rs.getString("Authors"));
                    row.put("Language", rs.getString("Language"));
                    row.put("Format", rs.getString("Format"));
                    results.add(row);
                }
            }
        }
        return results;
    }
}
