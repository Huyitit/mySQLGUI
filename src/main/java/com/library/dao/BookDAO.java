package com.library.dao;

import com.library.database.ConnectionManager;
import com.library.models.Book;

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
     * Find all books
     */
    public List<Book> findAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT BookId, Name, Language, FilePath, Format, UserId, PublisherId FROM BOOK ORDER BY Name";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("BookId"));
                book.setName(rs.getString("Name"));
                book.setLanguage(rs.getString("Language"));
                book.setFilePath(rs.getString("FilePath"));
                book.setFormat(rs.getString("Format"));
                book.setUserId(rs.getInt("UserId"));
                book.setPublisherId(rs.getInt("PublisherId"));
                books.add(book);
            }
        }
        return books;
    }
    
    /**
     * Find book by ID
     */
    public Book findById(int bookId) throws SQLException {
        String sql = "SELECT BookId, Name, Language, FilePath, Format, UserId, PublisherId FROM BOOK WHERE BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getInt("BookId"));
                    book.setName(rs.getString("Name"));
                    book.setLanguage(rs.getString("Language"));
                    book.setFilePath(rs.getString("FilePath"));
                    book.setFormat(rs.getString("Format"));
                    book.setUserId(rs.getInt("UserId"));
                    book.setPublisherId(rs.getInt("PublisherId"));
                    return book;
                }
            }
        }
        return null;
    }
    
    /**
     * Insert new book
     */
    public int insert(Book book) throws SQLException {
        String sql = "INSERT INTO BOOK (Name, Language, FilePath, Format, UserId, PublisherId) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getLanguage());
            stmt.setString(3, book.getFilePath());
            stmt.setString(4, book.getFormat());
            stmt.setInt(5, book.getUserId());
            stmt.setInt(6, book.getPublisherId());
            
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
     * Update existing book
     */
    public boolean update(Book book) throws SQLException {
        String sql = "UPDATE BOOK SET Name = ?, Language = ?, FilePath = ?, Format = ?, PublisherId = ? WHERE BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getLanguage());
            stmt.setString(3, book.getFilePath());
            stmt.setString(4, book.getFormat());
            stmt.setInt(5, book.getPublisherId());
            stmt.setInt(6, book.getBookId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete book by ID
     */
    public boolean delete(int bookId) throws SQLException {
        String sql = "DELETE FROM BOOK WHERE BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Search books by name
     */
    public List<Book> searchByName(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT BookId, Name, Language, FilePath, Format, UserId, PublisherId FROM BOOK WHERE Name LIKE ? ORDER BY Name";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + keyword + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getInt("BookId"));
                    book.setName(rs.getString("Name"));
                    book.setLanguage(rs.getString("Language"));
                    book.setFilePath(rs.getString("FilePath"));
                    book.setFormat(rs.getString("Format"));
                    book.setUserId(rs.getInt("UserId"));
                    book.setPublisherId(rs.getInt("PublisherId"));
                    books.add(book);
                }
            }
        }
        return books;
    }
    
    /**
     * Get books by user ID
     */
    public List<Book> findByUserId(int userId) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT BookId, Name, Language, FilePath, Format, UserId, PublisherId FROM BOOK WHERE UserId = ? ORDER BY Name";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book();
                    book.setBookId(rs.getInt("BookId"));
                    book.setName(rs.getString("Name"));
                    book.setLanguage(rs.getString("Language"));
                    book.setFilePath(rs.getString("FilePath"));
                    book.setFormat(rs.getString("Format"));
                    book.setUserId(rs.getInt("UserId"));
                    book.setPublisherId(rs.getInt("PublisherId"));
                    books.add(book);
                }
            }
        }
        return books;
    }
    
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
