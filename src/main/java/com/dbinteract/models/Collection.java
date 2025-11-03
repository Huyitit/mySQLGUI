package com.dbinteract.models;

import java.time.LocalDateTime;

/**
 * Collection entity representing COLLECTION table
 */
public class Collection {
    private int collectionId;
    private String collectionName;
    private LocalDateTime createdDate;
    private int userId;
    
    // Constructors
    public Collection() {}
    
    public Collection(String collectionName, int userId) {
        this.collectionName = collectionName;
        this.userId = userId;
    }
    
    public Collection(int collectionId, String collectionName, LocalDateTime createdDate, int userId) {
        this.collectionId = collectionId;
        this.collectionName = collectionName;
        this.createdDate = createdDate;
        this.userId = userId;
    }
    
    // Getters and Setters
    public int getCollectionId() {
        return collectionId;
    }
    
    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }
    
    public String getCollectionName() {
        return collectionName;
    }
    
    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "Collection{" +
                "collectionId=" + collectionId +
                ", collectionName='" + collectionName + '\'' +
                ", createdDate=" + createdDate +
                ", userId=" + userId +
                '}';
    }
}
