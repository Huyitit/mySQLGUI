package com.library.ui;

import com.library.dao.UserBookDAO;
import com.library.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Book Ratings Panel: View book ratings and average scores
 */
public class BookRatingsPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JLabel statusLabel;
    private JTextField bookTitleField;
    private JTextArea resultArea;
    
    public BookRatingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("⭐ Query 5: Book Ratings");
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
        
        JLabel sqlLabel = new JLabel("<html><b>SQL:</b> SELECT AVG(ub.UserRating) AS AverageRating, COUNT(ub.UserRating) AS TotalRatings<br>" +
                "FROM USERBOOK ub JOIN BOOK b ... WHERE b.Name = ? AND ub.UserRating IS NOT NULL</html>");
        sqlLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sqlPanel.add(sqlLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setOpaque(false);
        controlPanel.add(new JLabel("Book Title:"));
        
        bookTitleField = new JTextField(30);
        bookTitleField.setText("Clean Code");
        controlPanel.add(bookTitleField);
        
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
    
    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 16));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBackground(new Color(236, 240, 241));
        resultArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        resultArea.setText("Enter a book title and click 'Execute Query' to see its rating...");
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(Color.WHITE);
        
        statusLabel = new JLabel("Ready to execute query");
        panel.add(statusLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void executeQuery() {
        String bookTitle = bookTitleField.getText().trim();
        if (bookTitle.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a book title!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        statusLabel.setText("Executing query...");
        resultArea.setText("Loading...");
        
        SwingWorker<Map<String, Object>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Object> doInBackground() throws Exception {
                UserBookDAO dao = new UserBookDAO();
                return dao.getBookRating(bookTitle);
            }
            
            @Override
            protected void done() {
                try {
                    Map<String, Object> result = get();
                    
                    if (result.isEmpty()) {
                        resultArea.setText("No ratings found for book: " + bookTitle);
                        statusLabel.setText("No results found");
                        return;
                    }
                    
                    double avgRating = (Double) result.get("AverageRating");
                    int totalRatings = (Integer) result.get("TotalRatings");
                    
                    StringBuilder sb = new StringBuilder();
                    sb.append("═══════════════════════════════════════\n\n");
                    sb.append("📖  Book: ").append(bookTitle).append("\n\n");
                    sb.append("═══════════════════════════════════════\n\n");
                    
                    if (totalRatings == 0) {
                        sb.append("⭐  No ratings yet\n\n");
                        sb.append("This book hasn't been rated by any users yet.");
                    } else {
                        sb.append("⭐  Average Rating: ").append(String.format("%.2f", avgRating)).append(" / 5.0\n\n");
                        sb.append(generateStarDisplay(avgRating)).append("\n\n");
                        sb.append("👥  Total Ratings: ").append(totalRatings).append(" user(s)\n\n");
                        sb.append("═══════════════════════════════════════\n\n");
                        sb.append(getRatingDescription(avgRating));
                    }
                    
                    resultArea.setText(sb.toString());
                    statusLabel.setText("Query executed successfully");
                    
                } catch (Exception e) {
                    resultArea.setText("Error executing query:\n" + e.getMessage());
                    statusLabel.setText("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private String generateStarDisplay(double rating) {
        int fullStars = (int) rating;
        boolean halfStar = (rating - fullStars) >= 0.5;
        
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < fullStars; i++) {
            stars.append("★ ");
        }
        if (halfStar) {
            stars.append("⯨ ");
        }
        for (int i = fullStars + (halfStar ? 1 : 0); i < 5; i++) {
            stars.append("☆ ");
        }
        
        return stars.toString();
    }
    
    private String getRatingDescription(double rating) {
        if (rating >= 4.5) {
            return "🎉 Excellent! Highly recommended by users.";
        } else if (rating >= 4.0) {
            return "👍 Very Good! Users really enjoyed this book.";
        } else if (rating >= 3.5) {
            return "😊 Good! A solid choice.";
        } else if (rating >= 3.0) {
            return "🤔 Average. Mixed reviews from users.";
        } else {
            return "😕 Below Average. Consider other options.";
        }
    }
}
