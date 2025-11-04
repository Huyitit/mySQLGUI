package com.library.models;

/**
 * Genre entity representing GENRE table
 */
public class Genre {
    private int genreId;
    private String genreName;
    
    // Constructors
    public Genre() {}
    
    public Genre(String genreName) {
        this.genreName = genreName;
    }
    
    public Genre(int genreId, String genreName) {
        this.genreId = genreId;
        this.genreName = genreName;
    }
    
    // Getters and Setters
    public int getGenreId() {
        return genreId;
    }
    
    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }
    
    public String getGenreName() {
        return genreName;
    }
    
    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
    
    @Override
    public String toString() {
        return "Genre{" +
                "genreId=" + genreId +
                ", genreName='" + genreName + '\'' +
                '}';
    }
}
