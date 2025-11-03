package com.dbinteract.dao;

import com.dbinteract.database.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for BOOKMARK table
 */
public class BookmarkDAO {
    
    /**
     * QUERY 3: Get all bookmarks for a specific user and book
     */
    public List<Map<String, Object>> getUserBookmarksForBook(String username, String bookTitle) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String sql = "SELECT " +
                "    bm.BookmarkName, " +
                "    bm.Location, " +
                "    bm.CreatedDate " +
                "FROM BOOKMARK bm " +
                "JOIN USERBOOK ub ON bm.UserBookId = ub.UserBookId " +
                "JOIN USER u ON ub.UserId = u.UserId " +
                "JOIN BOOK b ON ub.BookId = b.BookId " +
                "WHERE u.UserName = ? AND b.Name = ? " +
                "ORDER BY bm.CreatedDate DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, bookTitle);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("BookmarkName", rs.getString("BookmarkName"));
                    row.put("Location", rs.getString("Location"));
                    
                    Timestamp created = rs.getTimestamp("CreatedDate");
                    row.put("CreatedDate", created != null ? created.toString() : "");
                    
                    results.add(row);
                }
            }
        }
        return results;
    }
}
