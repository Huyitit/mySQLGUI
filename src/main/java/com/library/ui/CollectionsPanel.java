package com.library.ui;

import com.library.dao.CollectionDAO;
import com.library.models.Collection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Collections Management Panel
 * View and manage all collections
 */
public class CollectionsPanel extends JPanel {
    
    private MainFrame mainFrame;
    private CollectionDAO collectionDAO;
    
    private JTable collectionsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton, backButton, deleteButton;
    
    public CollectionsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.collectionDAO = new CollectionDAO();
        initializeUI();
        loadCollections();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Table
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Bottom buttons
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📁 Collections Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        backButton = new JButton("← Back to Dashboard");
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainFrame.showPanel("dashboard"));
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(backButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("📌 All collections from all users"));
        
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadCollections());
        infoPanel.add(refreshButton);
        
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Collection Name", "User ID", "Created Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        collectionsTable = new JTable(tableModel);
        collectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        collectionsTable.setRowHeight(25);
        collectionsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(collectionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        
        deleteButton = new JButton("🗑️ Delete Collection");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteCollection());
        
        panel.add(deleteButton);
        
        return panel;
    }
    
    private void loadCollections() {
        try {
            List<Collection> collections = collectionDAO.findAll();
            tableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (Collection collection : collections) {
                String createdDate = collection.getCreatedDate() != null ?
                    collection.getCreatedDate().format(formatter) : "N/A";
                
                tableModel.addRow(new Object[]{
                    collection.getCollectionId(),
                    collection.getCollectionName(),
                    collection.getUserId(),
                    createdDate
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading collections: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteCollection() {
        int selectedRow = collectionsTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a collection to delete!",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int collectionId = (int) tableModel.getValueAt(selectedRow, 0);
        String collectionName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete collection '" + collectionName + "'?\n" +
            "This will remove all books from this collection!",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (collectionDAO.delete(collectionId)) {
                    JOptionPane.showMessageDialog(this,
                        "Collection deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCollections();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting collection: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
