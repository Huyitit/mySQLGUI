package com.library.models;

/**
 * Publisher entity representing PUBLISHER table
 */
public class Publisher {
    private int publisherId;
    private String publisherName;
    
    // Constructors
    public Publisher() {}
    
    public Publisher(String publisherName) {
        this.publisherName = publisherName;
    }
    
    public Publisher(int publisherId, String publisherName) {
        this.publisherId = publisherId;
        this.publisherName = publisherName;
    }
    
    // Getters and Setters
    public int getPublisherId() {
        return publisherId;
    }
    
    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }
    
    public String getPublisherName() {
        return publisherName;
    }
    
    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }
    
    @Override
    public String toString() {
        return "Publisher{" +
                "publisherId=" + publisherId +
                ", publisherName='" + publisherName + '\'' +
                '}';
    }
}
