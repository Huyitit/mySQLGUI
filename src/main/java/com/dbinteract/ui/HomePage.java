package com.dbinteract.ui;

import com.dbinteract.utils.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Modern home page for the Book Library application
 */
public class HomePage extends JPanel {
    
    private MainFrame mainFrame;
    private JLabel userLabel;
    private JLabel statsLabel;
    
    public HomePage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = createContentPanel();
        add(contentPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        // Gradient-like effect with darker blue
        panel.setBackground(new Color(30, 58, 138));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 40, 25, 40));
        
        // Left side - App title with icon
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        // Book icon background
        JLabel iconBg = new JLabel("■");
        iconBg.setFont(new Font("Segoe UI", Font.BOLD, 40));
        iconBg.setForeground(new Color(59, 130, 246));
        leftPanel.add(iconBg);
        
        JLabel titleLabel = new JLabel("Book Library Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        // Right side - User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightPanel.setOpaque(false);
        
        // User info with background
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        userInfoPanel.setBackground(new Color(255, 255, 255, 20));
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        userLabel = new JLabel();
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        userInfoPanel.add(userLabel);
        
        rightPanel.add(userInfoPanel);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(239, 68, 68));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setPreferredSize(new Dimension(110, 40));
        logoutButton.setOpaque(true);
        logoutButton.addActionListener(e -> mainFrame.logout());
        
        // Hover effect for logout button
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(220, 38, 38));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(239, 68, 68));
            }
        });
        
        rightPanel.add(logoutButton);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(249, 250, 251));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        // Welcome section
        JPanel welcomePanel = createWelcomePanel();
        panel.add(welcomePanel, BorderLayout.NORTH);
        
        // Features grid
        JPanel featuresPanel = createFeaturesPanel();
        panel.add(featuresPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(249, 250, 251));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));
        
        JLabel welcomeLabel = new JLabel("Welcome to Your Personal Library");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(17, 24, 39));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(welcomeLabel);
        
        panel.add(Box.createVerticalStrut(12));
        
        statsLabel = new JLabel("Manage your books, track your reading progress, and discover new titles");
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        statsLabel.setForeground(new Color(107, 114, 128));
        statsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statsLabel);
        
        panel.add(Box.createVerticalStrut(5));
        
        // Decorative line
        JPanel separator = new JPanel();
        separator.setBackground(new Color(229, 231, 235));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(separator);
        
        return panel;
    }
    
    private JPanel createFeaturesPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 25, 25));
        panel.setBackground(new Color(249, 250, 251));
        
        // Feature cards with modern design
        panel.add(createFeatureCard(
            "My Library",
            "View and manage your book collection with reading progress",
            new Color(59, 130, 246),
            new Color(219, 234, 254),
            Constants.PANEL_QUERY1
        ));
        
        panel.add(createFeatureCard(
            "Browse Books",
            "Explore books by genre and category across our database",
            new Color(147, 51, 234),
            new Color(233, 213, 255),
            Constants.PANEL_QUERY2
        ));
        
        panel.add(createFeatureCard(
            "My Bookmarks",
            "Access your saved bookmarks and notes for quick reference",
            new Color(249, 115, 22),
            new Color(254, 215, 170),
            Constants.PANEL_QUERY3
        ));
        
        panel.add(createFeatureCard(
            "My Collections",
            "Organize books into custom collections and lists",
            new Color(16, 185, 129),
            new Color(209, 250, 229),
            Constants.PANEL_QUERY4
        ));
        
        panel.add(createFeatureCard(
            "Popular Books",
            "Discover trending and highly-rated books from the community",
            new Color(239, 68, 68),
            new Color(254, 202, 202),
            Constants.PANEL_QUERY6
        ));
        
        panel.add(createFeatureCard(
            "Add Book",
            "Add new books to the library database",
            new Color(34, 197, 94),
            new Color(187, 247, 208),
            Constants.PANEL_QUERY7
        ));
        
        return panel;
    }
    
    private JPanel createFeatureCard(String title, String description, Color color, Color lightColor, String panelName) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 2),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Title with colored bar
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        
        // Colored indicator bar
        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(4, 28));
        titlePanel.add(colorBar);
        titlePanel.add(Box.createHorizontalStrut(12));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(17, 24, 39));
        titlePanel.add(titleLabel);
        
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(15));
        
        // Description
        JLabel descLabel = new JLabel("<html><p style='width:280px'>" + description + "</p></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descLabel);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        // Bottom panel with arrow
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomPanel.setOpaque(false);
        
        JLabel arrowLabel = new JLabel("Open →");
        arrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        arrowLabel.setForeground(color);
        bottomPanel.add(arrowLabel);
        
        card.add(bottomPanel, BorderLayout.SOUTH);
        
        // Hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(lightColor);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color, 3),
                    BorderFactory.createEmptyBorder(24, 24, 24, 24)
                ));
                arrowLabel.setText("Open →→");
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(229, 231, 235), 2),
                    BorderFactory.createEmptyBorder(25, 25, 25, 25)
                ));
                arrowLabel.setText("Open →");
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainFrame.showPanel(panelName);
            }
        });
        
        return card;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(243, 244, 246));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel footerLabel = new JLabel("Book Library Manager v1.0  •  Your Personal Reading Companion  •  © 2025");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        footerLabel.setForeground(new Color(107, 114, 128));
        panel.add(footerLabel);
        
        return panel;
    }
    
    public void updateUserInfo() {
        if (mainFrame.getCurrentUser() != null) {
            userLabel.setText(mainFrame.getCurrentUser().getUserName());
        }
    }
}
