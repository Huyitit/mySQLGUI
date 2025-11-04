package com.library.ui;

import com.library.utils.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard panel for Library Management System
 * Main menu for managing books, users, authors, genres, publishers, collections, and reports
 */
public class DashboardPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JLabel userLabel;
    
    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel with queries
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📚 " + Constants.APP_TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        userLabel = new JLabel();
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        rightPanel.add(userLabel);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> mainFrame.logout());
        rightPanel.add(logoutButton);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        
        // Title
        JLabel titleLabel = new JLabel("LIBRARY MANAGEMENT MENU:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, gbc);
        
        // Management menu buttons
        String[] menuItems = {
            "� Books Management - Add, edit, delete, and search books",
            "� Users Management - Manage user accounts",
            "✍️ Authors Management - Manage book authors",
            "🏷️ Genres Management - Manage book categories",
            "🏢 Publishers Management - Manage publishers",
            "� Collections Management - Manage book collections",
            "📊 Reports & Statistics - View database statistics"
        };
        
        String[] panelNames = {
            Constants.PANEL_BOOKS,
            Constants.PANEL_USERS,
            Constants.PANEL_AUTHORS,
            Constants.PANEL_GENRES,
            Constants.PANEL_PUBLISHERS,
            Constants.PANEL_COLLECTIONS,
            Constants.PANEL_REPORTS
        };
        
        for (int i = 0; i < menuItems.length; i++) {
            gbc.gridy = i + 1;
            JButton btn = createMenuButton(menuItems[i], panelNames[i]);
            panel.add(btn, gbc);
        }
        
        // Stats panel at bottom
        gbc.gridy = 8;
        gbc.insets = new Insets(30, 10, 10, 10);
        JPanel statsPanel = createStatsPanel();
        panel.add(statsPanel, gbc);
        
        return panel;
    }
    
    private JButton createMenuButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(650, 50));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(Color.BLACK);
            }
        });
        
        button.addActionListener(e -> mainFrame.showPanel(panelName));
        
        return button;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("ℹ️ ABOUT THIS SYSTEM:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(10));
        
        JLabel infoLabel = new JLabel("<html>" +
                "• <b>Library Management System (Demo)</b><br>" +
                "• Full CRUD operations for all entities<br>" +
                "• Click any menu item above to start managing<br>" +
                "• All changes are saved to the database immediately" +
                "</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(infoLabel);
        
        return panel;
    }
    
    public void updateUserInfo() {
        if (mainFrame.getCurrentUser() != null) {
            userLabel.setText("👤 " + mainFrame.getCurrentUser().getUserName());
        }
    }
}
