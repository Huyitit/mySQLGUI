package com.dbinteract.ui;

import com.dbinteract.dao.UserBookDAO;
import com.dbinteract.utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Query 1 Panel: User's Library with status and progress
 */
public class Query1Panel extends JPanel {
    
    private MainFrame mainFrame;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    public Query1Panel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
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
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📖 Query 1: My Library");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JButton backButton = new JButton("← Back");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainFrame.showPanel(Constants.PANEL_DASHBOARD));
        rightPanel.add(backButton);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        // SQL Query info
        JPanel sqlPanel = new JPanel(new BorderLayout());
        sqlPanel.setBackground(new Color(236, 240, 241));
        sqlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        JLabel sqlLabel = new JLabel("<html><b>SQL:</b> SELECT b.Name, a.AuthorName, ub.Progress, ub.UserRating, ub.LastReadDate<br>" +
                "FROM USERBOOK ub JOIN BOOK b ... WHERE u.UserName = ?</html>");
        sqlLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sqlPanel.add(sqlLabel, BorderLayout.CENTER);
        
        JButton executeButton = new JButton("Execute Query");
        executeButton.setBackground(new Color(39, 174, 96));
        executeButton.setForeground(Color.WHITE);
        executeButton.setBorderPainted(false);
        executeButton.setFocusPainted(false);
        executeButton.addActionListener(e -> executeQuery());
        sqlPanel.add(executeButton, BorderLayout.EAST);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(sqlPanel, BorderLayout.SOUTH);
        
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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(Color.WHITE);
        
        statusLabel = new JLabel("Click 'Execute Query' to view your library");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
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
                        String ratingStr = rating != null ? "⭐".repeat((Integer) rating) : "☆☆☆☆☆";
                        
                        tableModel.addRow(new Object[]{
                            row.get("BookTitle"),
                            row.get("Authors"),
                            row.get("Progress"),
                            ratingStr,
                            row.get("LastReadDate")
                        });
                    }
                    
                    statusLabel.setText("Results: " + results.size() + " books found");
                    
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(Query1Panel.this,
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
