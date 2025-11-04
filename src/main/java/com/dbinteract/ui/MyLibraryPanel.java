package com.dbinteract.ui;

import com.dbinteract.dao.UserBookDAO;
import com.dbinteract.utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * My Library Panel: User's Library with status and progress
 */
public class MyLibraryPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public MyLibraryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    /**
     * Load data when panel becomes visible
     */
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
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Table
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Bottom panel with status and back button
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 152, 219));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("My Library");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        // Right side - Back button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JButton backButton = new JButton("Back to Home");
        backButton.setBackground(new Color(41, 128, 185));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.setOpaque(true);
        backButton.addActionListener(e -> mainFrame.showPanel(Constants.PANEL_HOME));
        rightPanel.add(backButton);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        // Subtitle panel
        JPanel subtitlePanel = new JPanel(new BorderLayout());
        subtitlePanel.setBackground(new Color(240, 248, 255));
        subtitlePanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JLabel subtitleLabel = new JLabel("View and manage your book collection with reading progress and ratings");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(52, 73, 94));
        subtitlePanel.add(subtitleLabel, BorderLayout.WEST);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(new Color(39, 174, 96));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(110, 32));
        refreshButton.setOpaque(true);
        refreshButton.addActionListener(e -> executeQuery());
        subtitlePanel.add(refreshButton, BorderLayout.EAST);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(subtitlePanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {"Book Title", "Authors", "Progress", "Rating", "Last Read"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(30);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        resultTable.getTableHeader().setBackground(new Color(52, 73, 94));
        resultTable.getTableHeader().setForeground(Color.WHITE);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        return new JScrollPane(resultTable);
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        panel.setBackground(new Color(236, 240, 241));
        
        statusLabel = new JLabel("Loading your library...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(52, 73, 94));
        panel.add(statusLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void executeQuery() {
        if (mainFrame.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this, "No user logged in!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        statusLabel.setText("Executing query...");
        tableModel.setRowCount(0);
        
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                UserBookDAO dao = new UserBookDAO();
                return dao.getUserLibraryWithDetails(mainFrame.getCurrentUser().getUserName());
            }
            
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> results = get();
                    
                    for (Map<String, Object> row : results) {
                        Object rating = row.get("UserRating");
                        String ratingStr = rating != null ? "*".repeat((Integer) rating) : "-----";
                        
                        tableModel.addRow(new Object[]{
                            row.get("BookTitle"),
                            row.get("Authors"),
                            row.get("Progress"),
                            ratingStr,
                            row.get("LastReadDate")
                        });
                    }
                    
                    statusLabel.setText(results.size() + " book" + (results.size() != 1 ? "s" : "") + " in your library");
                    
                    if (results.isEmpty()) {
                        statusLabel.setText("Your library is empty. Start adding books!");
                    }
                    
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(MyLibraryPanel.this,
                        "Failed to execute query: " + e.getMessage(),
                        "Query Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
}
