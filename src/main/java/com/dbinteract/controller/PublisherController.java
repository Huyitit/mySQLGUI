package com.dbinteract.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dbinteract.dao.PublisherDAO;
import com.dbinteract.models.Publisher;

@RestController
@RequestMapping("/api/publishers")
public class PublisherController {
    
    private final PublisherDAO publisherDAO;
    
    public PublisherController(PublisherDAO publisherDAO) {
        this.publisherDAO = publisherDAO;
    }
    
    @GetMapping
    public ResponseEntity<List<Publisher>> getAllPublishers() {
        try {
            List<Publisher> publishers = publisherDAO.findAll();
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Publisher>> searchPublishers(@RequestParam String q) {
        try {
            List<Publisher> publishers = publisherDAO.searchByName(q);
            return ResponseEntity.ok(publishers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
