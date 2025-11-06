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
import com.dbinteract.models.User;
import com.dbinteract.utils.PasswordHasher;

/**
 * Data Access Object for USER table
 */
@Repository
public class UserDAO {
    
    /**
     * Find user by username
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM USER WHERE UserName = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Find user by ID
     */
    public User findById(int userId) throws SQLException {
        String sql = "SELECT * FROM USER WHERE UserId = ?";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Authenticate user
     */
    public User authenticate(String username, String password) throws SQLException {
        User user = findByUsername(username);
        
        if (user != null) {
            System.out.println("User found: " + user.getUserName());
            if (PasswordHasher.verifyPassword(password, user.getHashedPassword())) {
                System.out.println("Password verified for user: " + user.getUserName());
                return user;
            }
        }
        
        return null;
    }
    
    /**
     * Get all users
     */
    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USER ORDER BY UserName";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }
    
    /**
     * Insert new user
     */
    public int insert(User user) throws SQLException {
        String sql = "INSERT INTO USER (UserName, HashedPassword) VALUES (?, ?)";
        
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getHashedPassword());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Delete user by ID
     */
    public boolean deleteById(int userId) throws SQLException {
        String sql = "DELETE FROM USER WHERE UserId = ?";
        try (Connection conn = ConnectionManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int affected = stmt.executeUpdate();
            return affected > 0;
        }
    }
    
    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("UserId"));
        user.setUserName(rs.getString("UserName"));
        user.setHashedPassword(rs.getString("HashedPassword"));
        return user;
    }
}
