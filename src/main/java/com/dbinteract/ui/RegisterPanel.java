package com.dbinteract.ui;

import com.dbinteract.dao.UserDAO;
import com.dbinteract.models.User;
import com.dbinteract.utils.Constants;
import com.dbinteract.utils.PasswordHasher;

import javax.swing.*;
import java.awt.*;

/**
 * Registration panel for creating new user accounts
 */
public class RegisterPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;
    private JLabel messageLabel;
    
    public RegisterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        JLabel titleLabel = new JLabel("📝 Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Register a new user account");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 20, 5);
        add(subtitleLabel, gbc);
        
        // Username label
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(new JLabel("Username:"), gbc);
        
        // Username field
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(usernameField, gbc);
        
        // Username requirements
        JLabel usernameReqLabel = new JLabel("3-100 characters");
        usernameReqLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        usernameReqLabel.setForeground(Color.GRAY);
        gbc.gridy = 3;
        add(usernameReqLabel, gbc);
        
        // Password label
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Password:"), gbc);
        
        // Password field
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);
        
        // Password requirements
        JLabel passwordReqLabel = new JLabel("Minimum 6 characters");
        passwordReqLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        passwordReqLabel.setForeground(Color.GRAY);
        gbc.gridy = 5;
        add(passwordReqLabel, gbc);
        
        // Confirm Password label
        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Confirm Password:"), gbc);
        
        // Confirm Password field
        confirmPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(confirmPasswordField, gbc);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        // Back button
        backButton = new JButton("← Back to Login");
        backButton.setPreferredSize(new Dimension(140, 35));
        backButton.setBackground(new Color(149, 165, 166));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setFont(new Font("Arial", Font.PLAIN, 13));
        backButton.addActionListener(e -> mainFrame.showPanel(Constants.PANEL_LOGIN));
        buttonsPanel.add(backButton);
        
        // Register button
        registerButton = new JButton("REGISTER");
        registerButton.setPreferredSize(new Dimension(120, 35));
        registerButton.setBackground(new Color(39, 174, 96));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.addActionListener(e -> handleRegister());
        buttonsPanel.add(registerButton);
        
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        add(buttonsPanel, gbc);
        
        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 5, 5, 5);
        add(messageLabel, gbc);
        
        // Info label
        JLabel infoLabel = new JLabel("<html><center>After registration, you can login with your new account</center></html>");
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        gbc.gridy = 9;
        gbc.insets = new Insets(20, 5, 5, 5);
        add(infoLabel, gbc);
        
        // Enable Enter key for registration
        confirmPasswordField.addActionListener(e -> handleRegister());
    }
    
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }
        
        if (username.length() < 3 || username.length() > 100) {
            showError("Username must be between 3 and 100 characters");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }
        
        // Check for invalid characters in username
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("Username can only contain letters, numbers, and underscore");
            return;
        }
        
        registerButton.setEnabled(false);
        registerButton.setText("Registering...");
        messageLabel.setText("Creating account...");
        messageLabel.setForeground(Color.BLUE);
        
        // Perform registration in background thread
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String errorMessage = null;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                UserDAO userDAO = new UserDAO();
                
                // Check if username already exists
                User existingUser = userDAO.findByUsername(username);
                if (existingUser != null) {
                    errorMessage = "Username already exists. Please choose another one.";
                    return false;
                }
                
                // Create new user with hashed password
                User newUser = new User();
                newUser.setUserName(username);
                newUser.setHashedPassword(PasswordHasher.hashPassword(password));
                
                // Insert into database
                int userId = userDAO.insert(newUser);
                
                return userId > 0;
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    
                    if (success) {
                        showSuccess("Account created successfully! You can now login.");
                        
                        // Clear fields
                        clearFields();
                        
                        // Redirect to login after 2 seconds
                        Timer timer = new Timer(2000, e -> {
                            mainFrame.showPanel(Constants.PANEL_LOGIN);
                        });
                        timer.setRepeats(false);
                        timer.start();
                        
                    } else {
                        showError(errorMessage != null ? errorMessage : "Registration failed");
                    }
                    
                } catch (Exception e) {
                    showError("Error: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    registerButton.setEnabled(true);
                    registerButton.setText("REGISTER");
                }
            }
        };
        
        worker.execute();
    }
    
    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setForeground(new Color(231, 76, 60));
    }
    
    private void showSuccess(String message) {
        messageLabel.setText("✅ " + message);
        messageLabel.setForeground(new Color(39, 174, 96));
    }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        messageLabel.setText(" ");
    }
}
