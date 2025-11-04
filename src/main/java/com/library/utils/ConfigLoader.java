package com.library.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to load configuration from config.properties
 */
public class ConfigLoader {
    
    private static Properties properties = new Properties();
    private static boolean loaded = false;
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            
            if (input == null) {
                System.err.println("Unable to find config.properties");
                return;
            }
            
            properties.load(input);
            loaded = true;
            System.out.println("Configuration loaded successfully");
            
        } catch (IOException ex) {
            System.err.println("Error loading configuration: " + ex.getMessage());
        }
    }
    
    /**
     * Get a property value as String
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Get a property value as String with default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get a property value as Integer
     */
    public static int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Check if configuration is loaded successfully
     */
    public static boolean isLoaded() {
        return loaded;
    }
    
    /**
     * Get database connection URL
     */
    public static String getDatabaseUrl() {
        String host = getProperty(Constants.CONFIG_DB_HOST, "localhost");
        int port = getIntProperty(Constants.CONFIG_DB_PORT, 3306);
        String dbName = getProperty(Constants.CONFIG_DB_NAME, "lib_system");
        
        return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                host, port, dbName);
    }
    
    /**
     * Get database username
     */
    public static String getDatabaseUsername() {
        return getProperty(Constants.CONFIG_DB_USERNAME, "root");
    }
    
    /**
     * Get database password
     */
    public static String getDatabasePassword() {
        return getProperty(Constants.CONFIG_DB_PASSWORD, "123456");
    }
}
