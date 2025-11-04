package com.library.ui;

import com.library.dao.BookDAO;
import com.library.dao.GenreDAO;
import com.library.models.Genre;
import com.library.utils.Constants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Browse by Genre Panel: Find and browse books by category/genre
 */
public class BrowseByGenrePanel extends JPanel {
    
    private MainFrame mainFrame;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JComboBox<String> genreComboBox;
    
    public BrowseByGenrePanel(MainFrame mainFrame) {
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
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("🔍 Query 2: Browse by Genre");
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
        
        // SQL and controls panel
        JPanel sqlPanel = new JPanel(new BorderLayout());
        sqlPanel.setBackground(new Color(236, 240, 241));
        sqlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        
        JLabel sqlLabel = new JLabel("<html><b>SQL:</b> SELECT b.Name, a.AuthorName, p.PublisherName<br>" +
                "FROM BOOK b JOIN GENREBOOK gb ... WHERE g.GenreName = ?</html>");
        sqlLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        sqlPanel.add(sqlLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setOpaque(false);
        controlPanel.add(new JLabel("Select Genre:"));
        
        genreComboBox = new JComboBox<>();
        genreComboBox.setPreferredSize(new Dimension(200, 30));
        controlPanel.add(genreComboBox);
        
        JButton executeButton = new JButton("Execute Query");
        executeButton.setBackground(new Color(39, 174, 96));
        executeButton.setForeground(Color.WHITE);
        executeButton.setBorderPainted(false);
        executeButton.setFocusPainted(false);
        executeButton.addActionListener(e -> executeQuery());
        controlPanel.add(executeButton);
        
        sqlPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.add(sqlPanel, BorderLayout.SOUTH);
        
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
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.setBackground(Color.WHITE);
        
        statusLabel = new JLabel("Select a genre and click 'Execute Query'");
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
                    
                    statusLabel.setText("Results: " + results.size() + " books found in genre '" + selectedGenre + "'");
                    
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(BrowseByGenrePanel.this,
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
