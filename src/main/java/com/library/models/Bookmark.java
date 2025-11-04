package com.library.models;

import java.time.LocalDateTime;

/**
 * Bookmark entity representing BOOKMARK table
 */
public class Bookmark {
    private int bookmarkId;
    private String bookmarkName;
    private String location;
    private LocalDateTime createdDate;
    private int userBookId;
    
    // Constructors
    public Bookmark() {}
    
    public Bookmark(String bookmarkName, String location, int userBookId) {
        this.bookmarkName = bookmarkName;
        this.location = location;
        this.userBookId = userBookId;
    }
    
    // Getters and Setters
    public int getBookmarkId() {
        return bookmarkId;
    }
    
    public void setBookmarkId(int bookmarkId) {
        this.bookmarkId = bookmarkId;
    }
    
    public String getBookmarkName() {
        return bookmarkName;
    }
    
    public void setBookmarkName(String bookmarkName) {
        this.bookmarkName = bookmarkName;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public int getUserBookId() {
        return userBookId;
    }
    
    public void setUserBookId(int userBookId) {
        this.userBookId = userBookId;
    }
    
    @Override
    public String toString() {
        return "Bookmark{" +
                "bookmarkId=" + bookmarkId +
                ", bookmarkName='" + bookmarkName + '\'' +
                ", location='" + location + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
