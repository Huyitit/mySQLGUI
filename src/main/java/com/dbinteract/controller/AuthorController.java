package com.dbinteract.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dbinteract.dao.AuthorDAO;
import com.dbinteract.models.Author;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    
    private final AuthorDAO authorDAO;
    
    public AuthorController(AuthorDAO authorDAO) {
        this.authorDAO = authorDAO;
    }
    
    @GetMapping
    public ResponseEntity<List<Author>> getAllAuthors() {
        try {
            List<Author> authors = authorDAO.findAll();
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Author>> searchAuthors(@RequestParam String q) {
        try {
            List<Author> authors = authorDAO.searchByName(q);
            return ResponseEntity.ok(authors);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
