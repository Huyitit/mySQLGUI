package com.dbinteract.ui;

import com.dbinteract.dao.*;
import com.dbinteract.models.*;
import com.dbinteract.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Query 7 Panel: Add New Book to Database
 * Allows users to insert their own books with authors and genres
 */
public class Query7Panel extends JPanel {
    
    private MainFrame mainFrame;
    private JLabel statusLabel;
    
    // Book information fields
    private JTextField bookTitleField;
    private JTextField languageField;
    private JTextField filePathField;
    private JComboBox<String> formatComboBox;
    private JComboBox<String> publisherComboBox;
    
    // Author selection
    private JList<String> authorList;
    private DefaultListModel<String> authorListModel;
    private List<Author> availableAuthors;
    private JTextField newAuthorField;
    
    // Genre selection
    private JList<String> genreList;
    private DefaultListModel<String> genreListModel;
    private List<Genre> availableGenres;
    private JTextField newGenreField;
    
    public Query7Panel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.availableAuthors = new ArrayList<>();
        this.availableGenres = new ArrayList<>();
        initializeUI();
        loadPublishers();
        loadAuthors();
        loadGenres();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Main content in scrollable panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("➕ Query 7: Add New Book");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        JButton backButton = new JButton("← Back");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setForeground(Color.WHITE);
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainFrame.showPanel(Constants.PANEL_DASHBOARD));
        panel.add(backButton, BorderLayout.EAST);
        
        mainPanel.add(panel, BorderLayout.NORTH);
        
        // SQL info panel
        JPanel sqlPanel = new JPanel(new BorderLayout());
        sqlPanel.setBackground(new Color(236, 240, 241));
        sqlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        JLabel sqlLabel = new JLabel("<html><b>SQL Operations:</b> INSERT INTO BOOK, AUTHORBOOK, GENREBOOK<br>" +
                "This form will add a new book and link it to authors and genres</html>");
        sqlLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sqlPanel.add(sqlLabel, BorderLayout.CENTER);
        
        mainPanel.add(sqlPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Section 1: Book Information
        panel.add(createSectionTitle("📖 STEP 1: Book Information"));
        panel.add(createBookInfoSection());
        panel.add(Box.createVerticalStrut(20));
        
        // Section 2: Select Authors
        panel.add(createSectionTitle("✍️ STEP 2: Select Authors (Required - at least 1)"));
        panel.add(createAuthorsSection());
        panel.add(Box.createVerticalStrut(20));
        
        // Section 3: Select Genres
        panel.add(createSectionTitle("📚 STEP 3: Select Genres (Optional)"));
        panel.add(createGenresSection());
        panel.add(Box.createVerticalStrut(20));
        
        return panel;
    }
    
    private JLabel createSectionTitle(String title) {
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return label;
    }
    
    private JPanel createBookInfoSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Book Title
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("*Book Title:"), gbc);
        bookTitleField = new JTextField(30);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(bookTitleField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(Required)"), gbc);
        row++;
        
        // Language
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Language:"), gbc);
        languageField = new JTextField(30);
        languageField.setText("English");
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(languageField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(e.g., English, Vietnamese)"), gbc);
        row++;
        
        // File Path
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("*File Path:"), gbc);
        filePathField = new JTextField(30);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(filePathField, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(e.g., /books/mybook.pdf)"), gbc);
        row++;
        
        // Format
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Format:"), gbc);
        formatComboBox = new JComboBox<>(new String[]{"PDF", "EPUB", "MOBI", "TXT", "DOCX"});
        formatComboBox.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(formatComboBox, gbc);
        row++;
        
        // Publisher
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.EAST;
        panel.add(new JLabel("Publisher:"), gbc);
        publisherComboBox = new JComboBox<>();
        publisherComboBox.setPreferredSize(new Dimension(200, 25));
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        panel.add(publisherComboBox, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("(Optional)"), gbc);
        
        return panel;
    }
    
    private JPanel createAuthorsSection() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoLabel = new JLabel("Select one or more authors. Hold Ctrl to select multiple.");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        // Author list
        authorListModel = new DefaultListModel<>();
        authorList = new JList<>(authorListModel);
        authorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        authorList.setVisibleRowCount(6);
        JScrollPane scrollPane = new JScrollPane(authorList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add new author section
        JPanel addAuthorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addAuthorPanel.setOpaque(false);
        addAuthorPanel.add(new JLabel("Or add new author:"));
        newAuthorField = new JTextField(20);
        addAuthorPanel.add(newAuthorField);
        JButton addAuthorButton = new JButton("Add Author");
        addAuthorButton.setBackground(new Color(52, 152, 219));
        addAuthorButton.setForeground(Color.WHITE);
        addAuthorButton.setBorderPainted(false);
        addAuthorButton.setFocusPainted(false);
        addAuthorButton.addActionListener(e -> addNewAuthor());
        addAuthorPanel.add(addAuthorButton);
        panel.add(addAuthorPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createGenresSection() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoLabel = new JLabel("Select one or more genres. Hold Ctrl to select multiple.");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        panel.add(infoLabel, BorderLayout.NORTH);
        
        // Genre list
        genreListModel = new DefaultListModel<>();
        genreList = new JList<>(genreListModel);
        genreList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        genreList.setVisibleRowCount(6);
        JScrollPane scrollPane = new JScrollPane(genreList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Add new genre section
        JPanel addGenrePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addGenrePanel.setOpaque(false);
        addGenrePanel.add(new JLabel("Or add new genre:"));
        newGenreField = new JTextField(20);
        addGenrePanel.add(newGenreField);
        JButton addGenreButton = new JButton("Add Genre");
        addGenreButton.setBackground(new Color(52, 152, 219));
        addGenreButton.setForeground(Color.WHITE);
        addGenreButton.setBorderPainted(false);
        addGenreButton.setFocusPainted(false);
        addGenreButton.addActionListener(e -> addNewGenre());
        addGenrePanel.add(addGenreButton);
        panel.add(addGenrePanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(Color.WHITE);
        
        statusLabel = new JLabel("Fill in the form and click 'Add Book' to insert into database");
        panel.add(statusLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton clearButton = new JButton("Clear Form");
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.WHITE);
        clearButton.setBorderPainted(false);
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> clearForm());
        buttonPanel.add(clearButton);
        
        JButton addButton = new JButton("Add Book to Database");
        addButton.setBackground(new Color(39, 174, 96));
        addButton.setForeground(Color.WHITE);
        addButton.setBorderPainted(false);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.addActionListener(e -> addBookToDatabase());
        buttonPanel.add(addButton);
        
        panel.add(buttonPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void loadPublishers() {
        SwingWorker<List<Publisher>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Publisher> doInBackground() throws Exception {
                PublisherDAO dao = new PublisherDAO();
                return dao.findAll();
            }
            
            @Override
            protected void done() {
                try {
                    List<Publisher> publishers = get();
                    publisherComboBox.addItem("-- Select Publisher --");
                    for (Publisher publisher : publishers) {
                        publisherComboBox.addItem(publisher.getPublisherName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void loadAuthors() {
        SwingWorker<List<Author>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Author> doInBackground() throws Exception {
                AuthorDAO dao = new AuthorDAO();
                return dao.findAll();
            }
            
            @Override
            protected void done() {
                try {
                    availableAuthors = get();
                    authorListModel.clear();
                    for (Author author : availableAuthors) {
                        authorListModel.addElement(author.getAuthorName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void loadGenres() {
        SwingWorker<List<Genre>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Genre> doInBackground() throws Exception {
                GenreDAO dao = new GenreDAO();
                return dao.findAll();
            }
            
            @Override
            protected void done() {
                try {
                    availableGenres = get();
                    genreListModel.clear();
                    for (Genre genre : availableGenres) {
                        genreListModel.addElement(genre.getGenreName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void addNewAuthor() {
        String authorName = newAuthorField.getText().trim();
        if (authorName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter author name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if already exists
        for (Author author : availableAuthors) {
            if (author.getAuthorName().equalsIgnoreCase(authorName)) {
                JOptionPane.showMessageDialog(this, "Author already exists in list", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        
        // Add to list
        Author newAuthor = new Author(authorName);
        newAuthor.setAuthorId(-1); // Temporary ID, will be inserted later
        availableAuthors.add(newAuthor);
        authorListModel.addElement(authorName);
        newAuthorField.setText("");
        statusLabel.setText("Author '" + authorName + "' added to list");
    }
    
    private void addNewGenre() {
        String genreName = newGenreField.getText().trim();
        if (genreName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter genre name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if already exists
        for (Genre genre : availableGenres) {
            if (genre.getGenreName().equalsIgnoreCase(genreName)) {
                JOptionPane.showMessageDialog(this, "Genre already exists in list", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        
        // Add to list
        Genre newGenre = new Genre(genreName);
        newGenre.setGenreId(-1); // Temporary ID, will be inserted later
        availableGenres.add(newGenre);
        genreListModel.addElement(genreName);
        newGenreField.setText("");
        statusLabel.setText("Genre '" + genreName + "' added to list");
    }
    
    private void addBookToDatabase() {
        // Validation
        String bookTitle = bookTitleField.getText().trim();
        String filePath = filePathField.getText().trim();
        
        if (bookTitle.isEmpty()) {
            showError("Book title is required");
            return;
        }
        
        if (filePath.isEmpty()) {
            showError("File path is required");
            return;
        }
        
        List<String> selectedAuthors = authorList.getSelectedValuesList();
        if (selectedAuthors.isEmpty()) {
            showError("Please select at least one author");
            return;
        }
        
        statusLabel.setText("Inserting book into database...");
        statusLabel.setForeground(Color.BLUE);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            String errorMessage = null;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                Connection conn = null;
                try {
                    conn = com.dbinteract.database.ConnectionManager.getInstance().getConnection();
                    conn.setAutoCommit(false); // Start transaction
                    
                    // STEP 1: Insert book
                    String insertBookSQL = "INSERT INTO BOOK (Name, Language, FilePath, Format, UserId, PublisherId) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement bookStmt = conn.prepareStatement(insertBookSQL, Statement.RETURN_GENERATED_KEYS);
                    
                    bookStmt.setString(1, bookTitle);
                    bookStmt.setString(2, languageField.getText().trim());
                    bookStmt.setString(3, filePath);
                    bookStmt.setString(4, (String) formatComboBox.getSelectedItem());
                    bookStmt.setInt(5, mainFrame.getCurrentUser().getUserId());
                    
                    // Publisher (optional)
                    String selectedPublisher = (String) publisherComboBox.getSelectedItem();
                    if (selectedPublisher != null && !selectedPublisher.startsWith("--")) {
                        // Find publisher ID
                        PublisherDAO pubDAO = new PublisherDAO();
                        List<Publisher> publishers = pubDAO.findAll();
                        Integer publisherId = null;
                        for (Publisher p : publishers) {
                            if (p.getPublisherName().equals(selectedPublisher)) {
                                publisherId = p.getPublisherId();
                                break;
                            }
                        }
                        if (publisherId != null) {
                            bookStmt.setInt(6, publisherId);
                        } else {
                            bookStmt.setNull(6, Types.INTEGER);
                        }
                    } else {
                        bookStmt.setNull(6, Types.INTEGER);
                    }
                    
                    bookStmt.executeUpdate();
                    
                    // Get generated book ID
                    ResultSet rs = bookStmt.getGeneratedKeys();
                    int bookId = -1;
                    if (rs.next()) {
                        bookId = rs.getInt(1);
                    }
                    rs.close();
                    bookStmt.close();
                    
                    if (bookId == -1) {
                        throw new SQLException("Failed to get book ID");
                    }
                    
                    // STEP 2: Insert/Link authors
                    for (String authorName : selectedAuthors) {
                        int authorId = -1;
                        
                        // Check if author exists in database
                        PreparedStatement checkAuthorStmt = conn.prepareStatement("SELECT AuthorId FROM AUTHOR WHERE AuthorName = ?");
                        checkAuthorStmt.setString(1, authorName);
                        ResultSet authorRS = checkAuthorStmt.executeQuery();
                        
                        if (authorRS.next()) {
                            authorId = authorRS.getInt("AuthorId");
                        } else {
                            // Insert new author
                            PreparedStatement insertAuthorStmt = conn.prepareStatement("INSERT INTO AUTHOR (AuthorName) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                            insertAuthorStmt.setString(1, authorName);
                            insertAuthorStmt.executeUpdate();
                            ResultSet authorKeys = insertAuthorStmt.getGeneratedKeys();
                            if (authorKeys.next()) {
                                authorId = authorKeys.getInt(1);
                            }
                            authorKeys.close();
                            insertAuthorStmt.close();
                        }
                        authorRS.close();
                        checkAuthorStmt.close();
                        
                        // Link book to author
                        PreparedStatement linkAuthorStmt = conn.prepareStatement("INSERT INTO AUTHORBOOK (AuthorId, BookId) VALUES (?, ?)");
                        linkAuthorStmt.setInt(1, authorId);
                        linkAuthorStmt.setInt(2, bookId);
                        linkAuthorStmt.executeUpdate();
                        linkAuthorStmt.close();
                    }
                    
                    // STEP 3: Insert/Link genres
                    List<String> selectedGenres = genreList.getSelectedValuesList();
                    for (String genreName : selectedGenres) {
                        int genreId = -1;
                        
                        // Check if genre exists in database
                        PreparedStatement checkGenreStmt = conn.prepareStatement("SELECT GenreId FROM GENRE WHERE GenreName = ?");
                        checkGenreStmt.setString(1, genreName);
                        ResultSet genreRS = checkGenreStmt.executeQuery();
                        
                        if (genreRS.next()) {
                            genreId = genreRS.getInt("GenreId");
                        } else {
                            // Insert new genre
                            PreparedStatement insertGenreStmt = conn.prepareStatement("INSERT INTO GENRE (GenreName) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                            insertGenreStmt.setString(1, genreName);
                            insertGenreStmt.executeUpdate();
                            ResultSet genreKeys = insertGenreStmt.getGeneratedKeys();
                            if (genreKeys.next()) {
                                genreId = genreKeys.getInt(1);
                            }
                            genreKeys.close();
                            insertGenreStmt.close();
                        }
                        genreRS.close();
                        checkGenreStmt.close();
                        
                        // Link book to genre
                        PreparedStatement linkGenreStmt = conn.prepareStatement("INSERT INTO GENREBOOK (GenreId, BookId) VALUES (?, ?)");
                        linkGenreStmt.setInt(1, genreId);
                        linkGenreStmt.setInt(2, bookId);
                        linkGenreStmt.executeUpdate();
                        linkGenreStmt.close();
                    }
                    
                    conn.commit(); // Commit transaction
                    return true;
                    
                } catch (Exception e) {
                    if (conn != null) {
                        try {
                            conn.rollback(); // Rollback on error
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    errorMessage = e.getMessage();
                    e.printStackTrace();
                    return false;
                } finally {
                    if (conn != null) {
                        try {
                            conn.setAutoCommit(true);
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            
            @Override
            protected void done() {
                try {
                    Boolean success = get();
                    
                    if (success) {
                        showSuccess("✅ Book '" + bookTitle + "' successfully added to database!");
                        clearForm();
                        loadAuthors(); // Refresh lists
                        loadGenres();
                    } else {
                        showError("Failed to add book: " + (errorMessage != null ? errorMessage : "Unknown error"));
                    }
                    
                } catch (Exception e) {
                    showError("Error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private void clearForm() {
        bookTitleField.setText("");
        languageField.setText("English");
        filePathField.setText("");
        formatComboBox.setSelectedIndex(0);
        publisherComboBox.setSelectedIndex(0);
        authorList.clearSelection();
        genreList.clearSelection();
        newAuthorField.setText("");
        newGenreField.setText("");
        statusLabel.setText("Form cleared. Ready to add new book.");
        statusLabel.setForeground(Color.BLACK);
    }
    
    private void showError(String message) {
        statusLabel.setText("❌ " + message);
        statusLabel.setForeground(new Color(231, 76, 60));
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showSuccess(String message) {
        statusLabel.setText(message);
        statusLabel.setForeground(new Color(39, 174, 96));
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
