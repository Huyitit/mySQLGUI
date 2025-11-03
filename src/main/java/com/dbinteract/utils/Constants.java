package com.dbinteract.utils;

/**
 * Application-wide constants
 */
public class Constants {
    
    // Database table names
    public static final String TABLE_USER = "USER";
    public static final String TABLE_BOOK = "BOOK";
    public static final String TABLE_AUTHOR = "AUTHOR";
    public static final String TABLE_GENRE = "GENRE";
    public static final String TABLE_PUBLISHER = "PUBLISHER";
    public static final String TABLE_COLLECTION = "COLLECTION";
    public static final String TABLE_USERBOOK = "USERBOOK";
    public static final String TABLE_BOOKMARK = "BOOKMARK";
    
    // UI Constants
    public static final String APP_TITLE = "Book Library Manager";
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 700;
    
    // Panel names (for CardLayout)
    public static final String PANEL_LOGIN = "login";
    public static final String PANEL_REGISTER = "register";
    public static final String PANEL_DASHBOARD = "dashboard";
    public static final String PANEL_QUERY1 = "query1";
    public static final String PANEL_QUERY2 = "query2";
    public static final String PANEL_QUERY3 = "query3";
    public static final String PANEL_QUERY4 = "query4";
    public static final String PANEL_QUERY5 = "query5";
    public static final String PANEL_QUERY6 = "query6";
    public static final String PANEL_QUERY7 = "query7";
    
    // Messages
    public static final String MSG_DB_CONNECTION_FAILED = "Failed to connect to database. Please check your configuration.";
    public static final String MSG_LOGIN_FAILED = "Invalid username or password.";
    public static final String MSG_LOGIN_SUCCESS = "Login successful!";
    public static final String MSG_QUERY_FAILED = "Failed to execute query.";
    
    // Config keys
    public static final String CONFIG_DB_HOST = "db.host";
    public static final String CONFIG_DB_PORT = "db.port";
    public static final String CONFIG_DB_NAME = "db.name";
    public static final String CONFIG_DB_USERNAME = "db.username";
    public static final String CONFIG_DB_PASSWORD = "db.password";

    private Constants() {
        // Prevent instantiation
    }
}
