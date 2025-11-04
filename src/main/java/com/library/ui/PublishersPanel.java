package com.library.ui;

import com.library.dao.PublisherDAO;
import com.library.models.Publisher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Publishers Management Panel
 * CRUD operations for publishers
 */
public class PublishersPanel extends JPanel {
    
    private MainFrame mainFrame;
    private PublisherDAO publisherDAO;
    
    private JTable publishersTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    
    public PublishersPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.publisherDAO = new PublisherDAO();
        initializeUI();
        loadPublishers();
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
        
        JLabel titleLabel = new JLabel("🏢 Publishers Management");
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
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("🔍 Search");
        searchButton.addActionListener(e -> searchPublishers());
        searchPanel.add(searchButton);
        
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadPublishers());
        searchPanel.add(refreshButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Publisher Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        publishersTable = new JTable(tableModel);
        publishersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        publishersTable.setRowHeight(25);
        publishersTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(publishersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        
        addButton = new JButton("➕ Add Publisher");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddDialog());
        
        editButton = new JButton("✏️ Edit Publisher");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setBorderPainted(false);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> showEditDialog());
        
        deleteButton = new JButton("🗑️ Delete Publisher");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deletePublisher());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        
        return panel;
    }
    
    private void loadPublishers() {
        try {
            List<Publisher> publishers = publisherDAO.findAll();
            tableModel.setRowCount(0);
            
            for (Publisher publisher : publishers) {
                tableModel.addRow(new Object[]{
                    publisher.getPublisherId(),
                    publisher.getPublisherName()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading publishers: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchPublishers() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadPublishers();
            return;
        }
        
        try {
            List<Publisher> publishers = publisherDAO.searchByName(keyword);
            tableModel.setRowCount(0);
            
            for (Publisher publisher : publishers) {
                tableModel.addRow(new Object[]{
                    publisher.getPublisherId(),
                    publisher.getPublisherName()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error searching publishers: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        JTextField nameField = new JTextField(20);
        
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(new JLabel("Publisher Name:"));
        panel.add(nameField);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Add New Publisher", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Publisher name cannot be empty!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Publisher publisher = new Publisher();
                publisher.setPublisherName(name);
                
                int publisherId = publisherDAO.insert(publisher);
                
                if (publisherId > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Publisher added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPublishers();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error adding publisher: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showEditDialog() {
        int selectedRow = publishersTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a publisher to edit!",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int publisherId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        
        JTextField nameField = new JTextField(currentName, 20);
        
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(new JLabel("Publisher Name:"));
        panel.add(nameField);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Edit Publisher", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Publisher name cannot be empty!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Publisher publisher = new Publisher();
                publisher.setPublisherId(publisherId);
                publisher.setPublisherName(newName);
                
                if (publisherDAO.update(publisher)) {
                    JOptionPane.showMessageDialog(this,
                        "Publisher updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPublishers();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error updating publisher: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deletePublisher() {
        int selectedRow = publishersTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a publisher to delete!",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int publisherId = (int) tableModel.getValueAt(selectedRow, 0);
        String publisherName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete publisher '" + publisherName + "'?\n" +
            "This may affect books associated with this publisher!",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (publisherDAO.delete(publisherId)) {
                    JOptionPane.showMessageDialog(this,
                        "Publisher deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPublishers();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting publisher: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
