package com.library.ui;

import com.library.dao.GenreDAO;
import com.library.models.Genre;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Genres Management Panel
 * CRUD operations for genres
 */
public class GenresPanel extends JPanel {
    
    private MainFrame mainFrame;
    private GenreDAO genreDAO;
    
    private JTable genresTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    
    public GenresPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.genreDAO = new GenreDAO();
        initializeUI();
        loadGenres();
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
        
        JLabel titleLabel = new JLabel("🏷️ Genres Management");
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
        searchButton.addActionListener(e -> searchGenres());
        searchPanel.add(searchButton);
        
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadGenres());
        searchPanel.add(refreshButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Genre Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        genresTable = new JTable(tableModel);
        genresTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        genresTable.setRowHeight(25);
        genresTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(genresTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        
        addButton = new JButton("➕ Add Genre");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddDialog());
        
        editButton = new JButton("✏️ Edit Genre");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setBorderPainted(false);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> showEditDialog());
        
        deleteButton = new JButton("🗑️ Delete Genre");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteGenre());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        
        return panel;
    }
    
    private void loadGenres() {
        try {
            List<Genre> genres = genreDAO.findAll();
            tableModel.setRowCount(0);
            
            for (Genre genre : genres) {
                tableModel.addRow(new Object[]{
                    genre.getGenreId(),
                    genre.getGenreName()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading genres: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchGenres() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadGenres();
            return;
        }
        
        try {
            List<Genre> genres = genreDAO.searchByName(keyword);
            tableModel.setRowCount(0);
            
            for (Genre genre : genres) {
                tableModel.addRow(new Object[]{
                    genre.getGenreId(),
                    genre.getGenreName()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error searching genres: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        JTextField nameField = new JTextField(20);
        
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(new JLabel("Genre Name:"));
        panel.add(nameField);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Add New Genre", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Genre name cannot be empty!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Genre genre = new Genre();
                genre.setGenreName(name);
                
                int genreId = genreDAO.insert(genre);
                
                if (genreId > 0) {
                    JOptionPane.showMessageDialog(this,
                        "Genre added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadGenres();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error adding genre: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showEditDialog() {
        int selectedRow = genresTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a genre to edit!",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int genreId = (int) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        
        JTextField nameField = new JTextField(currentName, 20);
        
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        panel.add(new JLabel("Genre Name:"));
        panel.add(nameField);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Edit Genre", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String newName = nameField.getText().trim();
            
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Genre name cannot be empty!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Genre genre = new Genre();
                genre.setGenreId(genreId);
                genre.setGenreName(newName);
                
                if (genreDAO.update(genre)) {
                    JOptionPane.showMessageDialog(this,
                        "Genre updated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadGenres();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error updating genre: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteGenre() {
        int selectedRow = genresTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a genre to delete!",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int genreId = (int) tableModel.getValueAt(selectedRow, 0);
        String genreName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete genre '" + genreName + "'?\n" +
            "This will also remove all book-genre associations!",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (genreDAO.delete(genreId)) {
                    JOptionPane.showMessageDialog(this,
                        "Genre deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadGenres();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting genre: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
