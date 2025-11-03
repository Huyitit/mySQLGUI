package com.dbinteract.ui;

import com.dbinteract.utils.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard panel showing 6 query options
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
        JLabel titleLabel = new JLabel("SELECT YOUR QUERY:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, gbc);
        
        // Query buttons
        String[] queryDescriptions = {
            "📖 Query 1: My Library - View my books with status & progress",
            "🔍 Query 2: Browse by Genre - Find books by category",
            "📑 Query 3: My Bookmarks - View my notes and highlights",
            "📂 Query 4: My Collections - Browse books in collections",
            "⭐ Query 5: Book Ratings - Check average ratings",
            "🔥 Query 6: Hot Books - Top 5 most popular books",
            "➕ Query 7: Add New Book - Insert your own book into database"
        };
        
        String[] panelNames = {
            Constants.PANEL_QUERY1,
            Constants.PANEL_QUERY2,
            Constants.PANEL_QUERY3,
            Constants.PANEL_QUERY4,
            Constants.PANEL_QUERY5,
            Constants.PANEL_QUERY6,
            Constants.PANEL_QUERY7
        };
        
        for (int i = 0; i < queryDescriptions.length; i++) {
            gbc.gridy = i + 1;
            JButton btn = createQueryButton(queryDescriptions[i], panelNames[i]);
            panel.add(btn, gbc);
        }
        
        // Stats panel at bottom
        gbc.gridy = 8;
        gbc.insets = new Insets(30, 10, 10, 10);
        JPanel statsPanel = createStatsPanel();
        panel.add(statsPanel, gbc);
        
        return panel;
    }
    
    private JButton createQueryButton(String text, String panelName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(600, 50));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(236, 240, 241));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
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
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("QUICK INFO:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel);
        
        panel.add(Box.createVerticalStrut(10));
        
        JLabel infoLabel = new JLabel("<html>" +
                "• All queries are pre-configured with sample data<br>" +
                "• Click on any query button above to execute<br>" +
                "• Use the Back button to return to this menu" +
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
