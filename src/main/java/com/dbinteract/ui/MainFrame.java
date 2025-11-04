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
    private HomePage homePage;
    private DashboardPanel dashboardPanel;
    private MyLibraryPanel myLibraryPanel;
    private BrowseBooksPanel browseBooksPanel;
    private MyBookmarksPanel myBookmarksPanel;
    private MyCollectionsPanel myCollectionsPanel;
    private BookRatingsPanel bookRatingsPanel;
    private PopularBooksPanel popularBooksPanel;
    private AddBookPanel addBookPanel;
    
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
        homePage = new HomePage(this);
        dashboardPanel = new DashboardPanel(this);
        myLibraryPanel = new MyLibraryPanel(this);
        browseBooksPanel = new BrowseBooksPanel(this);
        myBookmarksPanel = new MyBookmarksPanel(this);
        myCollectionsPanel = new MyCollectionsPanel(this);
        bookRatingsPanel = new BookRatingsPanel(this);
        popularBooksPanel = new PopularBooksPanel(this);
        addBookPanel = new AddBookPanel(this);
        
        // Add panels to card layout
        contentPanel.add(loginPanel, Constants.PANEL_LOGIN);
        contentPanel.add(registerPanel, Constants.PANEL_REGISTER);
        contentPanel.add(homePage, Constants.PANEL_HOME);
        contentPanel.add(dashboardPanel, Constants.PANEL_DASHBOARD);
        contentPanel.add(myLibraryPanel, Constants.PANEL_QUERY1);
        contentPanel.add(browseBooksPanel, Constants.PANEL_QUERY2);
        contentPanel.add(myBookmarksPanel, Constants.PANEL_QUERY3);
        contentPanel.add(myCollectionsPanel, Constants.PANEL_QUERY4);
        contentPanel.add(bookRatingsPanel, Constants.PANEL_QUERY5);
        contentPanel.add(popularBooksPanel, Constants.PANEL_QUERY6);
        contentPanel.add(addBookPanel, Constants.PANEL_QUERY7);
    }
    
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Update home page and dashboard with user info
        if (homePage != null && user != null) {
            homePage.updateUserInfo();
        }
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
