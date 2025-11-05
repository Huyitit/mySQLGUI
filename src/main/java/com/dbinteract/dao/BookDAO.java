package com.dbinteract.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.dbinteract.database.ConnectionManager;

/**
 * Data Access Object for BOOK table and related queries
 */
@Repository
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
    
    /**
     * Get all books
     */
    public List<com.dbinteract.models.Book> getAllBooks() throws SQLException {
        List<com.dbinteract.models.Book> books = new ArrayList<>();
        
        String sql = "SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId " +
                     "FROM BOOK ORDER BY Name";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                com.dbinteract.models.Book book = new com.dbinteract.models.Book();
                book.setBookId(rs.getInt("BookId"));
                book.setName(rs.getString("Name"));
                book.setLanguage(rs.getString("Language"));
                book.setFormat(rs.getString("Format"));
                book.setFilePath(rs.getString("FilePath"));
                books.add(book);
            }
        }
        
        return books;
    }
    
    /**
     * Get book by ID
     */
    public com.dbinteract.models.Book getBookById(int bookId) throws SQLException {
        String sql = "SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId " +
                     "FROM BOOK WHERE BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    com.dbinteract.models.Book book = new com.dbinteract.models.Book();
                    book.setBookId(rs.getInt("BookId"));
                    book.setName(rs.getString("Name"));
                    book.setLanguage(rs.getString("Language"));
                    book.setFormat(rs.getString("Format"));
                    book.setFilePath(rs.getString("FilePath"));
                    return book;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Get book details with authors
     */
    public Map<String, Object> getBookDetails(int bookId) throws SQLException {
        Map<String, Object> details = new HashMap<>();
        
        String sql = "SELECT b.BookId, b.Name, b.Language, b.Format, b.FilePath, " +
                     "p.PublisherName, " +
                     "GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors, " +
                     "GROUP_CONCAT(DISTINCT g.GenreName SEPARATOR ', ') AS Genres " +
                     "FROM BOOK b " +
                     "LEFT JOIN PUBLISHER p ON b.PublisherId = p.PublisherId " +
                     "LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId " +
                     "LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId " +
                     "LEFT JOIN GENREBOOK gb ON b.BookId = gb.BookId " +
                     "LEFT JOIN GENRE g ON gb.GenreId = g.GenreId " +
                     "WHERE b.BookId = ? " +
                     "GROUP BY b.BookId, b.Name, b.Language, b.Format, b.FilePath, p.PublisherName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    details.put("bookId", rs.getInt("BookId"));
                    details.put("name", rs.getString("Name"));
                    details.put("language", rs.getString("Language"));
                    details.put("format", rs.getString("Format"));
                    details.put("filePath", rs.getString("FilePath"));
                    details.put("publisher", rs.getString("PublisherName"));
                    details.put("authors", rs.getString("Authors"));
                    details.put("genres", rs.getString("Genres"));
                    
                    // Get file size if file exists
                    String filePath = rs.getString("FilePath");
                    if (filePath != null) {
                        try {
                            java.io.File file = new java.io.File(filePath);
                            if (file.exists()) {
                                long sizeInBytes = file.length();
                                double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
                                details.put("fileSize", String.format("%.2f MB", sizeInMB));
                            }
                        } catch (Exception e) {
                            details.put("fileSize", "Unknown");
                        }
                    }
                }
            }
        }
        
        return details;
    }
    
    /**
     * Search books by name
     */
    public List<com.dbinteract.models.Book> searchBooks(String query) throws SQLException {
        List<com.dbinteract.models.Book> books = new ArrayList<>();
        
        String sql = "SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId " +
                     "FROM BOOK WHERE Name LIKE ? ORDER BY Name";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + query + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    com.dbinteract.models.Book book = new com.dbinteract.models.Book();
                    book.setBookId(rs.getInt("BookId"));
                    book.setName(rs.getString("Name"));
                    book.setLanguage(rs.getString("Language"));
                    book.setFormat(rs.getString("Format"));
                    book.setFilePath(rs.getString("FilePath"));
                    books.add(book);
                }
            }
        }
        
        return books;
    }
    
    /**
     * Add a new book
     */
    public void addBook(com.dbinteract.models.Book book, int uploaderId) throws SQLException {
        String sql = "INSERT INTO BOOK (Name, Language, Format, FilePath, UserId, PublisherId) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getLanguage() != null ? book.getLanguage() : "Unknown");
            stmt.setString(3, book.getFormat());
            stmt.setString(4, book.getFilePath());
            stmt.setInt(5, uploaderId);
            
            if (book.getPublisherId() != null) {
                stmt.setInt(6, book.getPublisherId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    book.setBookId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    /**
     * Update an existing book
     */
    public boolean updateBook(com.dbinteract.models.Book book, int userId) throws SQLException {
        String sql = "UPDATE BOOK SET Name = ?, Language = ?, Format = ?, PublisherId = ? " +
                     "WHERE BookId = ? AND UserId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, book.getName());
            stmt.setString(2, book.getLanguage());
            stmt.setString(3, book.getFormat());
            
            if (book.getPublisherId() != null) {
                stmt.setInt(4, book.getPublisherId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            stmt.setInt(5, book.getBookId());
            stmt.setInt(6, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Delete a book
     */
    public boolean deleteBook(int bookId, int userId) throws SQLException {
        String sql = "DELETE FROM BOOK WHERE BookId = ? AND UserId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Add genres to a book
     */
    public void addGenresToBook(int bookId, List<Integer> genreIds) throws SQLException {
        if (genreIds == null || genreIds.isEmpty()) {
            return;
        }
        
        String sql = "INSERT IGNORE INTO GENREBOOK (GenreId, BookId) VALUES (?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Integer genreId : genreIds) {
                stmt.setInt(1, genreId);
                stmt.setInt(2, bookId);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
        }
    }
    
    /**
     * Add an author to a book
     */
    public void addAuthorToBook(int bookId, int authorId) throws SQLException {
        String sql = "INSERT IGNORE INTO AUTHORBOOK (AuthorId, BookId) VALUES (?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, authorId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Get all books uploaded by a specific user
     */
    public List<Map<String, Object>> getBooksByUploader(int userId) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();
        
        String sql = "SELECT " +
                "    b.BookId, " +
                "    b.Name AS title, " +
                "    GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS authors, " +
                "    p.PublisherName AS publisher, " +
                "    GROUP_CONCAT(DISTINCT g.GenreName SEPARATOR ', ') AS genres, " +
                "    b.Language, " +
                "    b.PublishedDate AS publishedDate, " +
                "    b.Format " +
                "FROM BOOK b " +
                "LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId " +
                "LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId " +
                "LEFT JOIN PUBLISHER p ON b.PublisherId = p.PublisherId " +
                "LEFT JOIN GENREBOOK gb ON b.BookId = gb.BookId " +
                "LEFT JOIN GENRE g ON gb.GenreId = g.GenreId " +
                "WHERE b.UploaderId = ? " +
                "GROUP BY b.BookId, b.Name, p.PublisherName, b.Language, b.PublishedDate, b.Format " +
                "ORDER BY b.BookId DESC";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> book = new HashMap<>();
                    book.put("bookId", rs.getInt("BookId"));
                    book.put("title", rs.getString("title"));
                    book.put("authors", rs.getString("authors"));
                    book.put("publisher", rs.getString("publisher"));
                    book.put("genres", rs.getString("genres"));
                    book.put("language", rs.getString("Language"));
                    book.put("publishedDate", rs.getDate("publishedDate") != null ? 
                        rs.getDate("publishedDate").toString() : null);
                    book.put("format", rs.getString("Format"));
                    results.add(book);
                }
            }
        }
        
        return results;
    }
}
