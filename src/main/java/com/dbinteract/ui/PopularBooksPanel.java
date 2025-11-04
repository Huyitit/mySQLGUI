package com.dbinteract.ui;

import com.dbinteract.dao.UserBookDAO;
import com.dbinteract.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Popular Books Panel: Top popular books
 */
public class PopularBooksPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JLabel statusLabel;
    private JSpinner limitSpinner;
    private JPanel resultsPanel;
    
    public PopularBooksPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            executeQuery();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Scrollable results panel
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        add(createBottomPanel(), BorderLayout.SOUTH);
        
        // Show initial message
        showInitialMessage();
    }
    
    private JPanel createHeaderPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(231, 76, 60));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Popular Books");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        JButton backButton = new JButton("Back to Home");
        backButton.setBackground(new Color(207, 70, 71));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.setOpaque(true);
        backButton.addActionListener(e -> mainFrame.showPanel(Constants.PANEL_HOME));
        panel.add(backButton, BorderLayout.EAST);
        
        mainPanel.add(panel, BorderLayout.NORTH);
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(new Color(240, 248, 255));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftControls.setOpaque(false);
        
        JLabel limitLabel = new JLabel("Show Top:");
        limitLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        leftControls.add(limitLabel);
        
        SpinnerNumberModel model = new SpinnerNumberModel(5, 1, 20, 1);
        limitSpinner = new JSpinner(model);
        limitSpinner.setPreferredSize(new Dimension(70, 35));
        limitSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        leftControls.add(limitSpinner);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(39, 174, 96));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(100, 35));
        refreshButton.setOpaque(true);
        refreshButton.addActionListener(e -> executeQuery());
        controlsPanel.add(refreshButton);
        
        controlsPanel.add(leftControls, BorderLayout.WEST);
        mainPanel.add(controlsPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        panel.setBackground(new Color(236, 240, 241));
        
        statusLabel = new JLabel("Loading popular books...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(52, 73, 94));
        panel.add(statusLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void showInitialMessage() {
        resultsPanel.removeAll();
        
        JLabel messageLabel = new JLabel("<html><center>" +
                "<h2>Discover the Most Popular Books</h2>" +
                "<p>Click 'Execute Query' to see which books are trending!</p>" +
                "</center></html>");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.GRAY);
        
        resultsPanel.add(Box.createVerticalGlue());
        resultsPanel.add(messageLabel);
        resultsPanel.add(Box.createVerticalGlue());
        
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }
    
    private void executeQuery() {
        int limit = (Integer) limitSpinner.getValue();
        
        statusLabel.setText("Executing query...");
        resultsPanel.removeAll();
        resultsPanel.add(new JLabel("Loading..."));
        resultsPanel.revalidate();
        resultsPanel.repaint();
        
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                UserBookDAO dao = new UserBookDAO();
                return dao.getTopPopularBooks(limit);
            }
            
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> results = get();
                    
                    resultsPanel.removeAll();
                    
                    if (results.isEmpty()) {
                        showInitialMessage();
                        statusLabel.setText("No books found");
                        return;
                    }
                    
                    for (Map<String, Object> row : results) {
                        JPanel bookCard = createBookCard(row);
                        resultsPanel.add(bookCard);
                        resultsPanel.add(Box.createVerticalStrut(15));
                    }
                    
                    resultsPanel.add(Box.createVerticalGlue());
                    resultsPanel.revalidate();
                    resultsPanel.repaint();
                    
                    statusLabel.setText("Results: Top " + results.size() + " books displayed");
                    
                } catch (Exception e) {
                    resultsPanel.removeAll();
                    JLabel errorLabel = new JLabel("Error: " + e.getMessage());
                    errorLabel.setForeground(Color.RED);
                    resultsPanel.add(errorLabel);
                    resultsPanel.revalidate();
                    resultsPanel.repaint();
                    
                    statusLabel.setText("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private JPanel createBookCard(Map<String, Object> bookData) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(800, 120));
        
        // Rank badge
        int rank = (Integer) bookData.get("Rank");
        JLabel rankLabel = new JLabel(getRankEmoji(rank));
        rankLabel.setFont(new Font("Arial", Font.BOLD, 48));
        rankLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        card.add(rankLabel, BorderLayout.WEST);
        
        // Book info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(bookData.get("BookTitle").toString());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        infoPanel.add(titleLabel);
        
        infoPanel.add(Box.createVerticalStrut(5));
        
        JLabel authorLabel = new JLabel("by " + bookData.get("Authors"));
        authorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        authorLabel.setForeground(Color.GRAY);
        infoPanel.add(authorLabel);
        
        infoPanel.add(Box.createVerticalStrut(10));
        
        int totalUsers = (Integer) bookData.get("TotalUsers");
        String avgRating = bookData.get("AverageRating").toString();
        
        JLabel statsLabel = new JLabel(totalUsers + " users added  |  " + avgRating + " avg rating");
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoPanel.add(statsLabel);
        
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private String getRankEmoji(int rank) {
        switch (rank) {
            case 1: return "🥇";
            case 2: return "🥈";
            case 3: return "🥉";
            case 4: return "4️⃣";
            case 5: return "5️⃣";
            default: return rank + "️⃣";
        }
    }
}
