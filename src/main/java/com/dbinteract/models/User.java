package com.dbinteract.models;

/**
 * User entity representing USER table
 */
public class User {
    private int userId;
    private String userName;
    private String hashedPassword;
    
    // Constructors
    public User() {}
    
    public User(String userName, String hashedPassword) {
        this.userName = userName;
        this.hashedPassword = hashedPassword;
    }
    
    public User(int userId, String userName, String hashedPassword) {
        this.userId = userId;
        this.userName = userName;
        this.hashedPassword = hashedPassword;
    }
    
    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getHashedPassword() {
        return hashedPassword;
    }
    
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
