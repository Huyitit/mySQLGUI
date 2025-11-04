package com.library.ui;

import com.library.dao.AuthorDAO;
import com.library.models.Author;
import com.library.utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Authors Management Panel - CRUD operations for authors
 */
public class AuthorsPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JTable authorsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    
    private AuthorDAO authorDAO;
    
    public AuthorsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.authorDAO = new AuthorDAO();
        
        initializeUI();
        loadAuthors();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("✍️ Authors Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.WEST);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("🔍 Search");
        searchButton.addActionListener(e -> searchAuthors());
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Author Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        authorsTable = new JTable(tableModel);
        authorsTable.setRowHeight(25);
        authorsTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        authorsTable.getColumnModel().getColumn(1).setPreferredWidth(400);
        authorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        return new JScrollPane(authorsTable);
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JButton addButton = new JButton("➕ Add Author");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddDialog());
        panel.add(addButton);
        
        JButton editButton = new JButton("✏️ Edit Author");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> showEditDialog());
        panel.add(editButton);
        
        JButton deleteButton = new JButton("🗑️ Delete Author");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteAuthor());
        panel.add(deleteButton);
        
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadAuthors());
        panel.add(refreshButton);
        
        JButton backButton = new JButton("◀ Back to Dashboard");
        backButton.addActionListener(e -> mainFrame.showPanel(Constants.PANEL_DASHBOARD));
        panel.add(backButton);
        
        return panel;
    }
    
    private void loadAuthors() {
        try {
            List<Author> authors = authorDAO.findAll();
            tableModel.setRowCount(0);
            for (Author author : authors) {
                tableModel.addRow(new Object[]{
                    author.getAuthorId(),
                    author.getAuthorName()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading authors: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchAuthors() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAuthors();
            return;
        }
        
        try {
            List<Author> authors = authorDAO.searchByName(keyword);
            tableModel.setRowCount(0);
            for (Author author : authors) {
                tableModel.addRow(new Object[]{
                    author.getAuthorId(),
                    author.getAuthorName()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching authors: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter author name:", "Add Author", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            try {
                Author author = new Author();
                author.setAuthorName(name.trim());
                authorDAO.insert(author);
                JOptionPane.showMessageDialog(this, "Author added successfully!");
                loadAuthors();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showEditDialog() {
        int selectedRow = authorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an author to edit", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int authorId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        
        String newName = (String) JOptionPane.showInputDialog(this, "Edit author name:", "Edit Author", 
            JOptionPane.PLAIN_MESSAGE, null, null, currentName);
        
        if (newName != null && !newName.trim().isEmpty()) {
            try {
                Author author = new Author();
                author.setAuthorId(authorId);
                author.setAuthorName(newName.trim());
                authorDAO.update(author);
                JOptionPane.showMessageDialog(this, "Author updated successfully!");
                loadAuthors();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteAuthor() {
        int selectedRow = authorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an author to delete", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int authorId = (int) tableModel.getValueAt(selectedRow, 0);
        String authorName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete: " + authorName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                authorDAO.delete(authorId);
                JOptionPane.showMessageDialog(this, "Author deleted successfully!");
                loadAuthors();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting author: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
