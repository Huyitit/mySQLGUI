package com.library.dao;

import com.library.database.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing many-to-many relationships
 * AUTHORBOOK, GENREBOOK, COLLECTIONBOOK tables
 */
public class RelationDAO {
    
    // ==================== BOOK-AUTHOR RELATIONS ====================
    
    /**
     * Add author to book
     */
    public boolean addAuthorToBook(int bookId, int authorId) throws SQLException {
        String sql = "INSERT INTO AUTHORBOOK (BookId, AuthorId) VALUES (?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Remove author from book
     */
    public boolean removeAuthorFromBook(int bookId, int authorId) throws SQLException {
        String sql = "DELETE FROM AUTHORBOOK WHERE BookId = ? AND AuthorId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get all author IDs for a book
     */
    public List<Integer> getAuthorIdsByBookId(int bookId) throws SQLException {
        List<Integer> authorIds = new ArrayList<>();
        String sql = "SELECT AuthorId FROM AUTHORBOOK WHERE BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    authorIds.add(rs.getInt("AuthorId"));
                }
            }
        }
        return authorIds;
    }
    
    /**
     * Get all book IDs for an author
     */
    public List<Integer> getBookIdsByAuthorId(int authorId) throws SQLException {
        List<Integer> bookIds = new ArrayList<>();
        String sql = "SELECT BookId FROM AUTHORBOOK WHERE AuthorId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, authorId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookIds.add(rs.getInt("BookId"));
                }
            }
        }
        return bookIds;
    }
    
    /**
     * Remove all authors from a book
     */
    public boolean removeAllAuthorsFromBook(int bookId) throws SQLException {
        String sql = "DELETE FROM AUTHORBOOK WHERE BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            return stmt.executeUpdate() >= 0;
        }
    }
    
    // ==================== BOOK-GENRE RELATIONS ====================
    
    /**
     * Add genre to book
     */
    public boolean addGenreToBook(int bookId, int genreId) throws SQLException {
        String sql = "INSERT INTO GENREBOOK (BookId, GenreId) VALUES (?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            stmt.setInt(2, genreId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Remove genre from book
     */
    public boolean removeGenreFromBook(int bookId, int genreId) throws SQLException {
        String sql = "DELETE FROM GENREBOOK WHERE BookId = ? AND GenreId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            stmt.setInt(2, genreId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get all genre IDs for a book
     */
    public List<Integer> getGenreIdsByBookId(int bookId) throws SQLException {
        List<Integer> genreIds = new ArrayList<>();
        String sql = "SELECT GenreId FROM GENREBOOK WHERE BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    genreIds.add(rs.getInt("GenreId"));
                }
            }
        }
        return genreIds;
    }
    
    /**
     * Get all book IDs for a genre
     */
    public List<Integer> getBookIdsByGenreId(int genreId) throws SQLException {
        List<Integer> bookIds = new ArrayList<>();
        String sql = "SELECT BookId FROM GENREBOOK WHERE GenreId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, genreId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookIds.add(rs.getInt("BookId"));
                }
            }
        }
        return bookIds;
    }
    
    /**
     * Remove all genres from a book
     */
    public boolean removeAllGenresFromBook(int bookId) throws SQLException {
        String sql = "DELETE FROM GENREBOOK WHERE BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            return stmt.executeUpdate() >= 0;
        }
    }
    
    // ==================== COLLECTION-BOOK RELATIONS ====================
    
    /**
     * Add book to collection
     */
    public boolean addBookToCollection(int collectionId, int bookId) throws SQLException {
        String sql = "INSERT INTO COLLECTIONBOOK (CollectionId, BookId) VALUES (?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            stmt.setInt(2, bookId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Remove book from collection
     */
    public boolean removeBookFromCollection(int collectionId, int bookId) throws SQLException {
        String sql = "DELETE FROM COLLECTIONBOOK WHERE CollectionId = ? AND BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            stmt.setInt(2, bookId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Get all book IDs in a collection
     */
    public List<Integer> getBookIdsByCollectionId(int collectionId) throws SQLException {
        List<Integer> bookIds = new ArrayList<>();
        String sql = "SELECT BookId FROM COLLECTIONBOOK WHERE CollectionId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookIds.add(rs.getInt("BookId"));
                }
            }
        }
        return bookIds;
    }
    
    /**
     * Get all collection IDs containing a book
     */
    public List<Integer> getCollectionIdsByBookId(int bookId) throws SQLException {
        List<Integer> collectionIds = new ArrayList<>();
        String sql = "SELECT CollectionId FROM COLLECTIONBOOK WHERE BookId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bookId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    collectionIds.add(rs.getInt("CollectionId"));
                }
            }
        }
        return collectionIds;
    }
    
    /**
     * Remove all books from a collection
     */
    public boolean removeAllBooksFromCollection(int collectionId) throws SQLException {
        String sql = "DELETE FROM COLLECTIONBOOK WHERE CollectionId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            return stmt.executeUpdate() >= 0;
        }
    }
    
    /**
     * Count books in a collection
     */
    public int countBooksInCollection(int collectionId) throws SQLException {
        String sql = "SELECT COUNT(*) as count FROM COLLECTIONBOOK WHERE CollectionId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, collectionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }
}
