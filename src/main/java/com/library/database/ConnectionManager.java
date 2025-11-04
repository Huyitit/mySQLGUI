package com.library.database;

import com.library.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to manage database connections
 */
public class ConnectionManager {
    
    private static ConnectionManager instance;
    private String url;
    private String username;
    private String password;
    
    private ConnectionManager() {
        this.url = ConfigLoader.getDatabaseUrl();
        this.username = ConfigLoader.getDatabaseUsername();
        this.password = ConfigLoader.getDatabasePassword();
    }
    
    /**
     * Get the singleton instance of ConnectionManager
     */
    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }
    
    /**
     * Get a new database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    
    /**
     * Test database connection
     * @return true if connection successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Close a connection safely
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
