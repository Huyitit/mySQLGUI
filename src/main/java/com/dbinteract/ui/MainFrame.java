package com.dbinteract.ui;

import com.dbinteract.models.User;
import com.dbinteract.utils.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window with CardLayout to switch between panels
 */
public class MainFrame extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private User currentUser;
    
    // Panels
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;
    private Query1Panel query1Panel;
    private Query2Panel query2Panel;
    private Query3Panel query3Panel;
    private Query4Panel query4Panel;
    private Query5Panel query5Panel;
    private Query6Panel query6Panel;
    private Query7Panel query7Panel;
    
    public MainFrame() {
        initializeFrame();
        initializePanels();
        showPanel(Constants.PANEL_LOGIN);
    }
    
    private void initializeFrame() {
        setTitle(Constants.APP_TITLE);
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Setup CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        add(contentPanel);
    }
    
    private void initializePanels() {
        // Initialize all panels
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        dashboardPanel = new DashboardPanel(this);
        query1Panel = new Query1Panel(this);
        query2Panel = new Query2Panel(this);
        query3Panel = new Query3Panel(this);
        query4Panel = new Query4Panel(this);
        query5Panel = new Query5Panel(this);
        query6Panel = new Query6Panel(this);
        query7Panel = new Query7Panel(this);
        
        // Add panels to card layout
        contentPanel.add(loginPanel, Constants.PANEL_LOGIN);
        contentPanel.add(registerPanel, Constants.PANEL_REGISTER);
        contentPanel.add(dashboardPanel, Constants.PANEL_DASHBOARD);
        contentPanel.add(query1Panel, Constants.PANEL_QUERY1);
        contentPanel.add(query2Panel, Constants.PANEL_QUERY2);
        contentPanel.add(query3Panel, Constants.PANEL_QUERY3);
        contentPanel.add(query4Panel, Constants.PANEL_QUERY4);
        contentPanel.add(query5Panel, Constants.PANEL_QUERY5);
        contentPanel.add(query6Panel, Constants.PANEL_QUERY6);
        contentPanel.add(query7Panel, Constants.PANEL_QUERY7);
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Update dashboard with user info
        if (dashboardPanel != null && user != null) {
            dashboardPanel.updateUserInfo();
        }
    }
    
    public void logout() {
        currentUser = null;
        showPanel(Constants.PANEL_LOGIN);
        loginPanel.clearFields();
    }
}
