package com.dbinteract.ui;

import com.dbinteract.dao.BookDAO;
import com.dbinteract.dao.GenreDAO;
import com.dbinteract.models.Genre;
import com.dbinteract.utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Browse Books Panel: Browse books by genre
 */
public class BrowseBooksPanel extends JPanel {
    
    private MainFrame mainFrame;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JComboBox<String> genreComboBox;
    
    public BrowseBooksPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initializeUI();
        loadGenres();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(155, 89, 182));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Left side - Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Browse Books");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        leftPanel.add(titleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        // Right side - Back button
        JButton backButton = new JButton("Back to Home");
        backButton.setBackground(new Color(123, 72, 147));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(150, 35));
        backButton.setOpaque(true);
        backButton.addActionListener(e -> mainFrame.showPanel(Constants.PANEL_HOME));
        panel.add(backButton, BorderLayout.EAST);
        
        mainPanel.add(panel, BorderLayout.NORTH);
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(new Color(240, 248, 255));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftControls.setOpaque(false);
        
        JLabel genreLabel = new JLabel("Select Genre:");
        genreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        leftControls.add(genreLabel);
        
        genreComboBox = new JComboBox<>();
        genreComboBox.setPreferredSize(new Dimension(200, 35));
        genreComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        leftControls.add(genreComboBox);
        
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(39, 174, 96));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchButton.setBorderPainted(false);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.setPreferredSize(new Dimension(100, 35));
        searchButton.setOpaque(true);
        searchButton.addActionListener(e -> executeQuery());
        leftControls.add(searchButton);
        
        controlsPanel.add(leftControls, BorderLayout.WEST);
        mainPanel.add(controlsPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JScrollPane createTablePanel() {
        String[] columns = {"Book Title", "Authors", "Publisher", "Language", "Format"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(30);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        resultTable.getTableHeader().setBackground(new Color(52, 73, 94));
        resultTable.getTableHeader().setForeground(Color.WHITE);
        
        return new JScrollPane(resultTable);
    }
    
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        panel.setBackground(new Color(236, 240, 241));
        
        statusLabel = new JLabel("Select a genre to browse books");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(52, 73, 94));
        panel.add(statusLabel, BorderLayout.WEST);
        
        return panel;
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
                    List<Genre> genres = get();
                    genreComboBox.removeAllItems();
                    for (Genre genre : genres) {
                        genreComboBox.addItem(genre.getGenreName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void executeQuery() {
        String selectedGenre = (String) genreComboBox.getSelectedItem();
        if (selectedGenre == null) {
            JOptionPane.showMessageDialog(this, "Please select a genre!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        statusLabel.setText("Executing query...");
        tableModel.setRowCount(0);
        
        SwingWorker<List<Map<String, Object>>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Map<String, Object>> doInBackground() throws Exception {
                BookDAO dao = new BookDAO();
                return dao.getBooksByGenre(selectedGenre);
            }
            
            @Override
            protected void done() {
                try {
                    List<Map<String, Object>> results = get();
                    
                    for (Map<String, Object> row : results) {
                        tableModel.addRow(new Object[]{
                            row.get("BookTitle"),
                            row.get("Authors"),
                            row.get("PublisherName"),
                            row.get("Language"),
                            row.get("Format")
                        });
                    }
                    
                    statusLabel.setText(results.size() + " book" + (results.size() != 1 ? "s" : "") + " found in '" + selectedGenre + "' genre");
                    
                    if (results.isEmpty()) {
                        statusLabel.setText("No books found in '" + selectedGenre + "' genre");
                    }
                    
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(BrowseBooksPanel.this,
                        "Failed to execute query: " + e.getMessage(),
                        "Query Error",
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
}
