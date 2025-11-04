package com.library.ui;

import com.library.dao.UserBookDAO;
import com.library.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Hot Books Panel: Display most popular books (Top 5 by user count)
 */
public class HotBooksPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JLabel statusLabel;
    private JSpinner limitSpinner;
    private JPanel resultsPanel;
    
    public HotBooksPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
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
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("🔥 Query 6: Hot Books (Most Popular)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        JButton backButton = new JButton("← Back");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainFrame.showPanel(Constants.PANEL_DASHBOARD));
        panel.add(backButton, BorderLayout.EAST);
        
        mainPanel.add(panel, BorderLayout.NORTH);
        
        // SQL and controls panel
        JPanel sqlPanel = new JPanel(new BorderLayout());
        sqlPanel.setBackground(new Color(236, 240, 241));
        sqlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        JLabel sqlLabel = new JLabel("<html><b>SELECT\n" + //
                        "    b.book_title,\n" + //
                        "    COUNT(lib.user_id) AS total_users\n" + //
                        "FROM user_library AS lib\n" + //
                        "JOIN book AS b ON lib.book_id = b.book_id\n" + //
                        "GROUP BY b.book_id\n" + //
                        "ORDER BY total_users DESC\n" + //
                        "LIMIT ?;</html>");
        sqlLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sqlPanel.add(sqlLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setOpaque(false);
        controlPanel.add(new JLabel("Top Limit:"));
        
        SpinnerNumberModel model = new SpinnerNumberModel(5, 1, 20, 1);
        limitSpinner = new JSpinner(model);
        limitSpinner.setPreferredSize(new Dimension(60, 30));
        controlPanel.add(limitSpinner);
        
        JButton executeButton = new JButton("Execute Query");
        executeButton.setBackground(new Color(39, 174, 96));
        executeButton.setForeground(Color.WHITE);
        executeButton.setBorderPainted(false);
        executeButton.setFocusPainted(false);
        executeButton.addActionListener(e -> executeQuery());
        controlPanel.add(executeButton);
        
        sqlPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.add(sqlPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(Color.WHITE);
        
        statusLabel = new JLabel("Click 'Execute Query' to view most popular books");
        panel.add(statusLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void showInitialMessage() {
        resultsPanel.removeAll();
        
        JLabel messageLabel = new JLabel("<html><center>" +
                "<h2>🔥 Discover the Most Popular Books</h2>" +
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
        
        JLabel statsLabel = new JLabel("👥 " + totalUsers + " users added  |  ⭐ " + avgRating + " avg rating");
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
