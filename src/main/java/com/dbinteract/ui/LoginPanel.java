package com.dbinteract.ui;

import com.dbinteract.dao.UserDAO;
import com.dbinteract.models.User;
import com.dbinteract.utils.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Login panel for user authentication
 */
public class LoginPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel;
    
    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        JLabel titleLabel = new JLabel(Constants.APP_TITLE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Please login to continue");
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
        
        // Password label
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JLabel("Password:"), gbc);
        
        // Password field
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(passwordField, gbc);
        
        // Login button
        loginButton = new JButton("LOGIN");
        loginButton.setPreferredSize(new Dimension(120, 35));
        loginButton.setBackground(new Color(52, 152, 219));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(e -> handleLogin());
        
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        add(loginButton, gbc);
        
        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 5, 5, 5);
        add(messageLabel, gbc);
        
        // Register link
        JButton registerButton = new JButton("Don't have an account? Register here");
        registerButton.setFont(new Font("Arial", Font.PLAIN, 12));
        registerButton.setForeground(new Color(52, 152, 219));
        registerButton.setBorderPainted(false);
        registerButton.setContentAreaFilled(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> mainFrame.showPanel(Constants.PANEL_REGISTER));
        gbc.gridy = 6;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(registerButton, gbc);
        
        // Enable Enter key for login
        passwordField.addActionListener(e -> handleLogin());
    }
    
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }
        
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        messageLabel.setText(" ");
        
        // Perform authentication in background thread
        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                UserDAO userDAO = new UserDAO();
                return userDAO.authenticate(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        mainFrame.setCurrentUser(user);
                        mainFrame.showPanel(Constants.PANEL_HOME);
                        clearFields();
                    } else {
                        messageLabel.setText(Constants.MSG_LOGIN_FAILED);
                    }
                } catch (Exception e) {
                    messageLabel.setText("Error: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("LOGIN");
                }
            }
        };
        
        worker.execute();
    }
    
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText(" ");
    }
}
