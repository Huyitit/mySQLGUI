package com.dbinteract.ui;

import com.dbinteract.dao.BookDAO;
import com.dbinteract.dao.CollectionDAO;
import com.dbinteract.utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * My Collections Panel: Books in a collection
 */
public class MyCollectionsPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JComboBox<String> collectionComboBox;
    
    public MyCollectionsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            loadCollections();
        }
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(26, 188, 156));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("My Collections");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        JButton backButton = new JButton("Back to Home");
        backButton.setBackground(new Color(23, 124, 122));
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
        
        JLabel collLabel = new JLabel("Select Collection:");
        collLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        leftControls.add(collLabel);
        
        collectionComboBox = new JComboBox<>();
        collectionComboBox.setPreferredSize(new Dimension(250, 35));
        collectionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        leftControls.add(collectionComboBox);
        
        JButton viewButton = new JButton("View Books");
        viewButton.setBackground(new Color(39, 174, 96));
        viewButton.setForeground(Color.WHITE);
        viewButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        viewButton.setBorderPainted(false);
        viewButton.setFocusPainted(false);
        viewButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewButton.setPreferredSize(new Dimension(120, 35));
        viewButton.setOpaque(true);
        viewButton.addActionListener(e -> executeQuery());
        leftControls.add(viewButton);
        
        controlsPanel.add(leftControls, BorderLayout.WEST);
        mainPanel.add(controlsPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {"Book Title", "Authors", "Language", "Format"};
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
        
        return new JScrollPane(resultTable);
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(Color.WHITE);
        
        statusLabel = new JLabel("Load your collections and select one to view books");
        panel.add(statusLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private void loadCollections() {
        if (mainFrame.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this, "No user logged in!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                CollectionDAO dao = new CollectionDAO();
                return dao.findCollectionNamesByUsername(mainFrame.getCurrentUser().getUserName());
            }
            
            @Override
            protected void done() {
                try {
                    List<String> collections = get();
                    collectionComboBox.removeAllItems();
                    for (String collection : collections) {
                        collectionComboBox.addItem(collection);
                    }
                    statusLabel.setText(collections.size() + " collections loaded. Select one and click 'Execute Query'");
                } catch (Exception e) {
                    statusLabel.setText("Error loading collections: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void executeQuery() {
        if (mainFrame.getCurrentUser() == null) {
            JOptionPane.showMessageDialog(this, "No user logged in!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String selectedCollection = (String) collectionComboBox.getSelectedItem();
        if (selectedCollection == null) {
            JOptionPane.showMessageDialog(this, "Please load and select a collection!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        statusLabel.setText("Executing query...");
        tableModel.setRowCount(0);
        
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                BookDAO dao = new BookDAO();
                return dao.getBooksInCollection(mainFrame.getCurrentUser().getUserName(), selectedCollection);
            }
            
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> results = get();
                    
                    for (Map<String, Object> row : results) {
                        tableModel.addRow(new Object[]{
                            row.get("BookTitle"),
                            row.get("Authors"),
                            row.get("Language"),
                            row.get("Format")
                        });
                    }
                    
                                        
                    statusLabel.setText(results.size() + " book" + (results.size() != 1 ? "s" : "") + " in collection '" + selectedCollection + "'");
                    
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(MyCollectionsPanel.this,
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
