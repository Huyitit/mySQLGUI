package com.library.ui;

import com.library.models.User;
import com.library.utils.Constants;

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
    
    // Old query panels (renamed to match their functions)
    private MyLibraryPanel myLibraryPanel;
    private BrowseByGenrePanel browseByGenrePanel;
    private MyBookmarksPanel myBookmarksPanel;
    private MyCollectionsPanel myCollectionsPanel;
    private BookRatingsPanel bookRatingsPanel;
    private HotBooksPanel hotBooksPanel;
    private AddBookPanel addBookPanel;
    
    // New management panels
    private BooksPanel booksPanel;
    private UsersPanel usersPanel;
    private AuthorsPanel authorsPanel;
    private GenresPanel genresPanel;
    private PublishersPanel publishersPanel;
    private CollectionsPanel collectionsPanel;
    private ReportsPanel reportsPanel;
    
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
        // Initialize core panels
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        dashboardPanel = new DashboardPanel(this);
        
        // Initialize old query panels (renamed to match their functions)
        myLibraryPanel = new MyLibraryPanel(this);
        browseByGenrePanel = new BrowseByGenrePanel(this);
        myBookmarksPanel = new MyBookmarksPanel(this);
        myCollectionsPanel = new MyCollectionsPanel(this);
        bookRatingsPanel = new BookRatingsPanel(this);
        hotBooksPanel = new HotBooksPanel(this);
        addBookPanel = new AddBookPanel(this);
        
        // Initialize new management panels
        booksPanel = new BooksPanel(this);
        usersPanel = new UsersPanel(this);
        authorsPanel = new AuthorsPanel(this);
        genresPanel = new GenresPanel(this);
        publishersPanel = new PublishersPanel(this);
        collectionsPanel = new CollectionsPanel(this);
        reportsPanel = new ReportsPanel(this);
        
        // Add core panels to card layout
        contentPanel.add(loginPanel, Constants.PANEL_LOGIN);
        contentPanel.add(registerPanel, Constants.PANEL_REGISTER);
        contentPanel.add(dashboardPanel, Constants.PANEL_DASHBOARD);
        
        // Add old query panels
        contentPanel.add(myLibraryPanel, Constants.PANEL_QUERY1);
        contentPanel.add(browseByGenrePanel, Constants.PANEL_QUERY2);
        contentPanel.add(myBookmarksPanel, Constants.PANEL_QUERY3);
        contentPanel.add(myCollectionsPanel, Constants.PANEL_QUERY4);
        contentPanel.add(bookRatingsPanel, Constants.PANEL_QUERY5);
        contentPanel.add(hotBooksPanel, Constants.PANEL_QUERY6);
        contentPanel.add(addBookPanel, Constants.PANEL_QUERY7);
        
        // Add new management panels
        contentPanel.add(booksPanel, Constants.PANEL_BOOKS);
        contentPanel.add(usersPanel, Constants.PANEL_USERS);
        contentPanel.add(authorsPanel, Constants.PANEL_AUTHORS);
        contentPanel.add(genresPanel, Constants.PANEL_GENRES);
        contentPanel.add(publishersPanel, Constants.PANEL_PUBLISHERS);
        contentPanel.add(collectionsPanel, Constants.PANEL_COLLECTIONS);
        contentPanel.add(reportsPanel, Constants.PANEL_REPORTS);
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
