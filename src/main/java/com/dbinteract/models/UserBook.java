package com.dbinteract.models;

import java.time.LocalDateTime;

/**
 * UserBook entity representing USERBOOK table
 */
public class UserBook {
    private int userBookId;
    private String progress;
    private LocalDateTime addedDate;
    private LocalDateTime lastReadDate;
    private Integer userRating;
    private int userId;
    private int bookId;
    
    // Constructors
    public UserBook() {}
    
    public UserBook(String progress, Integer userRating, int userId, int bookId) {
        this.progress = progress;
        this.userRating = userRating;
        this.userId = userId;
        this.bookId = bookId;
    }
    
    // Getters and Setters
    public int getUserBookId() {
        return userBookId;
    }
    
    public void setUserBookId(int userBookId) {
        this.userBookId = userBookId;
    }
    
    public String getProgress() {
        return progress;
    }
    
    public void setProgress(String progress) {
        this.progress = progress;
    }
    
    public LocalDateTime getAddedDate() {
        return addedDate;
    }
    
    public void setAddedDate(LocalDateTime addedDate) {
        this.addedDate = addedDate;
    }
    
    public LocalDateTime getLastReadDate() {
        return lastReadDate;
    }
    
    public void setLastReadDate(LocalDateTime lastReadDate) {
        this.lastReadDate = lastReadDate;
    }
    
    public Integer getUserRating() {
        return userRating;
    }
    
    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getBookId() {
        return bookId;
    }
    
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    
    @Override
    public String toString() {
        return "UserBook{" +
                "userBookId=" + userBookId +
                ", progress='" + progress + '\'' +
                ", userRating=" + userRating +
                ", userId=" + userId +
                ", bookId=" + bookId +
                '}';
    }
}
