package com.library.ui;

import com.library.dao.UserDAO;
import com.library.models.User;
import com.library.utils.PasswordHasher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Users Management Panel
 * CRUD operations for users (admin view)
 */
public class UsersPanel extends JPanel {
    
    private MainFrame mainFrame;
    private UserDAO userDAO;
    
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton, deleteButton, refreshButton, backButton;
    
    public UsersPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.userDAO = new UserDAO();
        initializeUI();
        loadUsers();
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
        
        JLabel titleLabel = new JLabel("👥 Users Management");
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
        searchButton.addActionListener(e -> searchUsers());
        searchPanel.add(searchButton);
        
        refreshButton = new JButton("🔄 Refresh");
        refreshButton.addActionListener(e -> loadUsers());
        searchPanel.add(refreshButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Username"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        usersTable = new JTable(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setRowHeight(25);
        usersTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        
        addButton = new JButton("➕ Add User");
        addButton.setBackground(new Color(46, 204, 113));
        addButton.setForeground(Color.WHITE);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showAddDialog());
        
        deleteButton = new JButton("🗑️ Delete User");
        deleteButton.setBackground(new Color(231, 76, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteUser());
        
        panel.add(addButton);
        panel.add(deleteButton);
        
        return panel;
    }
    
    private void loadUsers() {
        try {
            List<User> users = userDAO.findAll();
            tableModel.setRowCount(0);
            
            for (User user : users) {
                tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUserName()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading users: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchUsers() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadUsers();
            return;
        }
        
        try {
            List<User> users = userDAO.searchByUsername(keyword);
            tableModel.setRowCount(0);
            
            for (User user : users) {
                tableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUserName()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error searching users: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAddDialog() {
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmField = new JPasswordField(20);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmField);
        
        int result = JOptionPane.showConfirmDialog(this, panel,
            "Add New User", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());
            
            // Validation
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this,
                    "Passwords do not match!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this,
                    "Password must be at least 6 characters!",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Check if username exists
                if (userDAO.usernameExists(username)) {
                    JOptionPane.showMessageDialog(this,
                        "Username already exists!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                User user = new User();
                user.setUserName(username);
                user.setHashedPassword(PasswordHasher.hashPassword(password));
                
                int userId = userDAO.insert(user);
                
                if (userId > 0) {
                    JOptionPane.showMessageDialog(this,
                        "User added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error adding user: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a user to delete!",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Prevent deleting current user
        if (mainFrame.getCurrentUser() != null && 
            mainFrame.getCurrentUser().getUserId() == userId) {
            JOptionPane.showMessageDialog(this,
                "You cannot delete your own account!",
                "Operation Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user '" + username + "'?\n" +
            "This will also delete all books, collections, and bookmarks owned by this user!",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.delete(userId)) {
                    JOptionPane.showMessageDialog(this,
                        "User deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting user: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
