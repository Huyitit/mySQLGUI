package com.dbinteract.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dbinteract.dao.GenreDAO;
import com.dbinteract.models.Genre;

@RestController
@RequestMapping("/api/genres")
public class GenreController {
    
    private final GenreDAO genreDAO;
    
    public GenreController(GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
    }
    
    @GetMapping
    public ResponseEntity<List<Genre>> getAllGenres() {
        try {
            List<Genre> genres = genreDAO.findAll();
            return ResponseEntity.ok(genres);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Genre>> searchGenres(@RequestParam String q) {
        try {
            List<Genre> genres = genreDAO.searchByName(q);
            return ResponseEntity.ok(genres);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
