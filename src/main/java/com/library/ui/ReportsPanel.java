package com.library.ui;

import com.library.dao.StatisticsDAO;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Reports & Statistics Panel
 * Display database statistics and reports
 */
public class ReportsPanel extends JPanel {
    
    private MainFrame mainFrame;
    private StatisticsDAO statisticsDAO;
    
    private JButton backButton, refreshButton;
    private JPanel statsPanel;
    
    public ReportsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.statisticsDAO = new StatisticsDAO();
        initializeUI();
        loadStatistics();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Stats content
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scrollPane.setViewportView(statsPanel);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📊 Reports & Statistics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.setBackground(new Color(46, 204, 113));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadStatistics());
        rightPanel.add(refreshButton);
        
        backButton = new JButton("← Back to Dashboard");
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainFrame.showPanel("dashboard"));
        rightPanel.add(backButton);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void loadStatistics() {
        statsPanel.removeAll();
        
        try {
            // Overview section
            statsPanel.add(createSectionTitle("📈 DATABASE OVERVIEW"));
            statsPanel.add(createOverviewPanel());
            statsPanel.add(Box.createVerticalStrut(20));
            
            // Top genres
            statsPanel.add(createSectionTitle("🏆 TOP 5 GENRES"));
            statsPanel.add(createTopGenresPanel());
            statsPanel.add(Box.createVerticalStrut(20));
            
            // Top authors
            statsPanel.add(createSectionTitle("✍️ TOP 5 AUTHORS"));
            statsPanel.add(createTopAuthorsPanel());
            statsPanel.add(Box.createVerticalStrut(20));
            
            // Top publishers
            statsPanel.add(createSectionTitle("🏢 TOP 5 PUBLISHERS"));
            statsPanel.add(createTopPublishersPanel());
            statsPanel.add(Box.createVerticalStrut(20));
            
            // Books by format
            statsPanel.add(createSectionTitle("📚 BOOKS BY FORMAT"));
            statsPanel.add(createBooksByFormatPanel());
            statsPanel.add(Box.createVerticalStrut(20));
            
            // Books by language
            statsPanel.add(createSectionTitle("🌐 BOOKS BY LANGUAGE"));
            statsPanel.add(createBooksByLanguagePanel());
            
            statsPanel.revalidate();
            statsPanel.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading statistics: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JLabel createSectionTitle(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(new Color(52, 73, 94));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        try {
            panel.add(createStatCard("📚 Total Books", 
                String.valueOf(statisticsDAO.getTotalBooks()), 
                new Color(52, 152, 219)));
            
            panel.add(createStatCard("👥 Total Users", 
                String.valueOf(statisticsDAO.getTotalUsers()), 
                new Color(46, 204, 113)));
            
            panel.add(createStatCard("✍️ Total Authors", 
                String.valueOf(statisticsDAO.getTotalAuthors()), 
                new Color(155, 89, 182)));
            
            panel.add(createStatCard("🏷️ Total Genres", 
                String.valueOf(statisticsDAO.getTotalGenres()), 
                new Color(241, 196, 15)));
            
            panel.add(createStatCard("🏢 Total Publishers", 
                String.valueOf(statisticsDAO.getTotalPublishers()), 
                new Color(230, 126, 34)));
            
            panel.add(createStatCard("📁 Total Collections", 
                String.valueOf(statisticsDAO.getTotalCollections()), 
                new Color(231, 76, 60)));
                
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(color);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        
        return card;
    }
    
    private JPanel createTopGenresPanel() {
        try {
            return createTopItemsPanel(statisticsDAO.getTopGenres(5), 
                new Color(241, 196, 15));
        } catch (Exception e) {
            return createErrorPanel("Error loading genres: " + e.getMessage());
        }
    }
    
    private JPanel createTopAuthorsPanel() {
        try {
            return createTopItemsPanel(statisticsDAO.getTopAuthors(5), 
                new Color(155, 89, 182));
        } catch (Exception e) {
            return createErrorPanel("Error loading authors: " + e.getMessage());
        }
    }
    
    private JPanel createTopPublishersPanel() {
        try {
            return createTopItemsPanel(statisticsDAO.getTopPublishers(5), 
                new Color(230, 126, 34));
        } catch (Exception e) {
            return createErrorPanel("Error loading publishers: " + e.getMessage());
        }
    }
    
    private JPanel createBooksByFormatPanel() {
        try {
            return createTopItemsPanel(statisticsDAO.getBooksByFormat(), 
                new Color(52, 152, 219));
        } catch (Exception e) {
            return createErrorPanel("Error loading formats: " + e.getMessage());
        }
    }
    
    private JPanel createBooksByLanguagePanel() {
        try {
            return createTopItemsPanel(statisticsDAO.getBooksByLanguage(), 
                new Color(46, 204, 113));
        } catch (Exception e) {
            return createErrorPanel("Error loading languages: " + e.getMessage());
        }
    }
    
    private JPanel createErrorPanel(String message) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        JLabel label = new JLabel(message);
        label.setForeground(Color.RED);
        panel.add(label);
        return panel;
    }
    
    private JPanel createTopItemsPanel(Map<String, Integer> data, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (data.isEmpty()) {
            JLabel noDataLabel = new JLabel("No data available");
            noDataLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            noDataLabel.setForeground(Color.GRAY);
            panel.add(noDataLabel);
        } else {
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBackground(Color.WHITE);
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
                itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
                
                JLabel nameLabel = new JLabel(entry.getKey());
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                
                JLabel countLabel = new JLabel(String.valueOf(entry.getValue()) + " books");
                countLabel.setFont(new Font("Arial", Font.BOLD, 14));
                countLabel.setForeground(color);
                
                itemPanel.add(nameLabel, BorderLayout.WEST);
                itemPanel.add(countLabel, BorderLayout.EAST);
                
                panel.add(itemPanel);
                panel.add(Box.createVerticalStrut(5));
            }
        }
        
        return panel;
    }
}
