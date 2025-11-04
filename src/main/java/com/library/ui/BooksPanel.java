package com.library.ui;

import com.library.dao.*;
import com.library.models.*;
import com.library.utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Books Management Panel - CRUD operations for books
 */
public class BooksPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, editButton, deleteButton, refreshButton, backButton;
    
    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private GenreDAO genreDAO;
    private PublisherDAO publisherDAO;
    private RelationDAO relationDAO;
    
    public BooksPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.bookDAO = new BookDAO();
        this.authorDAO = new AuthorDAO();
        this.genreDAO = new GenreDAO();
        this.publisherDAO = new PublisherDAO();
        this.relationDAO = new RelationDAO();
        
        initializeUI();
        loadBooks();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Table
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Bottom buttons
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("📚 Books Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(Color.WHITE);
        
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("🔍 Search");
        searchButton.addActionListener(e -> searchBooks());
        searchPanel.add(searchButton);
        
        panel.add(searchPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Title", "Language", "Format", "Publisher", "User ID", "File Path"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        booksTable = new JTable(tableModel);
        booksTable.setRowHeight(25);
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        return new JScrollPane(booksTable);
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setBackground(Color.WHITE);
        
        addButton = new JButton("➕ Add Book");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddDialog());
        panel.add(addButton);
        
        editButton = new JButton("✏️ Edit Book");
        editButton.setBackground(new Color(52, 152, 219));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> showEditDialog());
        panel.add(editButton);
        
        deleteButton = new JButton("🗑️ Delete Book");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteBook());
        panel.add(deleteButton);
        
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadBooks());
        panel.add(refreshButton);
        
        backButton = new JButton("◀ Back to Dashboard");
        backButton.addActionListener(e -> mainFrame.showPanel(Constants.PANEL_DASHBOARD));
        panel.add(backButton);
        
        return panel;
    }
    
    private void loadBooks() {
        try {
            List<Book> books = bookDAO.findAll();
            tableModel.setRowCount(0);
            
            for (Book book : books) {
                String publisherName = "";
                if (book.getPublisherId() > 0) {
                    Publisher publisher = publisherDAO.findById(book.getPublisherId());
                    if (publisher != null) {
                        publisherName = publisher.getPublisherName();
                    }
                }
                
                tableModel.addRow(new Object[]{
                    book.getBookId(),
                    book.getName(),
                    book.getLanguage(),
                    book.getFormat(),
                    publisherName,
                    book.getUserId(),
                    book.getFilePath()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }
        
        try {
            List<Book> books = bookDAO.searchByName(keyword);
            tableModel.setRowCount(0);
            
            for (Book book : books) {
                String publisherName = "";
                if (book.getPublisherId() > 0) {
                    Publisher publisher = publisherDAO.findById(book.getPublisherId());
                    if (publisher != null) {
                        publisherName = publisher.getPublisherName();
                    }
                }
                
                tableModel.addRow(new Object[]{
                    book.getBookId(),
                    book.getName(),
                    book.getLanguage(),
                    book.getFormat(),
                    publisherName,
                    book.getUserId(),
                    book.getFilePath()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching books: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField titleField = new JTextField();
        JTextField languageField = new JTextField();
        JTextField filePathField = new JTextField();
        JComboBox<String> formatCombo = new JComboBox<>(new String[]{"PDF", "EPUB", "MOBI", "TXT"});
        
        JComboBox<String> publisherCombo = new JComboBox<>();
        try {
            List<Publisher> publishers = publisherDAO.findAll();
            for (Publisher p : publishers) {
                publisherCombo.addItem(p.getPublisherId() + " - " + p.getPublisherName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        JTextField userIdField = new JTextField(String.valueOf(mainFrame.getCurrentUser().getUserId()));
        
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Language:"));
        formPanel.add(languageField);
        formPanel.add(new JLabel("Format:"));
        formPanel.add(formatCombo);
        formPanel.add(new JLabel("Publisher:"));
        formPanel.add(publisherCombo);
        formPanel.add(new JLabel("User ID:"));
        formPanel.add(userIdField);
        formPanel.add(new JLabel("File Path:"));
        formPanel.add(filePathField);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> {
            try {
                Book book = new Book();
                book.setName(titleField.getText());
                book.setLanguage(languageField.getText());
                book.setFormat((String) formatCombo.getSelectedItem());
                
                String publisherStr = (String) publisherCombo.getSelectedItem();
                int publisherId = Integer.parseInt(publisherStr.split(" - ")[0]);
                book.setPublisherId(publisherId);
                
                book.setUserId(Integer.parseInt(userIdField.getText()));
                book.setFilePath(filePathField.getText());
                
                bookDAO.insert(book);
                JOptionPane.showMessageDialog(dialog, "Book added successfully!");
                loadBooks();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void showEditDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        
        try {
            Book book = bookDAO.findById(bookId);
            if (book == null) {
                JOptionPane.showMessageDialog(this, "Book not found", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);
            
            JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
            formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JTextField titleField = new JTextField(book.getName());
            JTextField languageField = new JTextField(book.getLanguage());
            JTextField filePathField = new JTextField(book.getFilePath());
            JComboBox<String> formatCombo = new JComboBox<>(new String[]{"PDF", "EPUB", "MOBI", "TXT"});
            formatCombo.setSelectedItem(book.getFormat());
            
            JComboBox<String> publisherCombo = new JComboBox<>();
            List<Publisher> publishers = publisherDAO.findAll();
            for (Publisher p : publishers) {
                publisherCombo.addItem(p.getPublisherId() + " - " + p.getPublisherName());
                if (p.getPublisherId() == book.getPublisherId()) {
                    publisherCombo.setSelectedIndex(publisherCombo.getItemCount() - 1);
                }
            }
            
            JTextField userIdField = new JTextField(String.valueOf(book.getUserId()));
            userIdField.setEditable(false);
            
            formPanel.add(new JLabel("Title:"));
            formPanel.add(titleField);
            formPanel.add(new JLabel("Language:"));
            formPanel.add(languageField);
            formPanel.add(new JLabel("Format:"));
            formPanel.add(formatCombo);
            formPanel.add(new JLabel("Publisher:"));
            formPanel.add(publisherCombo);
            formPanel.add(new JLabel("User ID:"));
            formPanel.add(userIdField);
            formPanel.add(new JLabel("File Path:"));
            formPanel.add(filePathField);
            
            dialog.add(formPanel, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");
            
            saveButton.addActionListener(e -> {
                try {
                    book.setName(titleField.getText());
                    book.setLanguage(languageField.getText());
                    book.setFormat((String) formatCombo.getSelectedItem());
                    
                    String publisherStr = (String) publisherCombo.getSelectedItem();
                    int publisherId = Integer.parseInt(publisherStr.split(" - ")[0]);
                    book.setPublisherId(publisherId);
                    
                    book.setFilePath(filePathField.getText());
                    
                    bookDAO.update(book);
                    JOptionPane.showMessageDialog(dialog, "Book updated successfully!");
                    loadBooks();
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.setVisible(true);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete: " + bookTitle + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookDAO.delete(bookId);
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");
                loadBooks();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
