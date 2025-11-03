package com.dbinteract;

import com.dbinteract.database.ConnectionManager;
import com.dbinteract.ui.MainFrame;
import com.dbinteract.utils.Constants;

import javax.swing.*;

/**
 * Main application entry point
 */
public class Main {
    
    public static void main(String[] args) {
        // Set Look and Feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set Look and Feel: " + e.getMessage());
        }
        
        // Test database connection
        System.out.println("Testing database connection...");
        if (!ConnectionManager.getInstance().testConnection()) {
            JOptionPane.showMessageDialog(null,
                    Constants.MSG_DB_CONNECTION_FAILED + "\n\nPlease check:\n" +
                    "1. MySQL server is running\n" +
                    "2. Database 'book_library' exists (run database.sql)\n" +
                    "3. config.properties has correct credentials",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        System.out.println("Database connection successful!");
        
        // Launch the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
