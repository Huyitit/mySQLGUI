package com.dbinteract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Main Spring Boot Application Class
 * Entry point for the Book Library Web Application
 */
@SpringBootApplication
@EnableConfigurationProperties
public class BookLibraryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BookLibraryApplication.class, args);
        System.out.println("📚 Book Library Web Application Started!");
        System.out.println("🌐 Access at: http://localhost:8080");
        System.out.println("📖 API Docs: http://localhost:8080/api/");
    }
}
