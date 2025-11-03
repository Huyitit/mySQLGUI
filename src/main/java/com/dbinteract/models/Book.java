package com.dbinteract.models;

/**
 * Book entity representing BOOK table
 */
public class Book {
    private int bookId;
    private String name;
    private String language;
    private String filePath;
    private String format;
    private Integer userId;
    private Integer publisherId;
    
    // Constructors
    public Book() {}
    
    public Book(String name, String language, String filePath, String format, 
                Integer userId, Integer publisherId) {
        this.name = name;
        this.language = language;
        this.filePath = filePath;
        this.format = format;
        this.userId = userId;
        this.publisherId = publisherId;
    }
    
    // Getters and Setters
    public int getBookId() {
        return bookId;
    }
    
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public Integer getPublisherId() {
        return publisherId;
    }
    
    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}
