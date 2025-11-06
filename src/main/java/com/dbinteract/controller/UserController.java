package com.dbinteract.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dbinteract.dao.UserDAO;
import com.dbinteract.security.UserPrincipal;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserDAO userDAO;

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Delete the authenticated user's account
     */
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteMyAccount() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserPrincipal) {
                int userId = ((UserPrincipal) principal).getUserId();
                System.out.println("Attempting to delete user with ID: " + userId);
                boolean deleted = userDAO.deleteById(userId);
                System.out.println("Delete result: " + deleted);
                if (deleted) {
                    return ResponseEntity.ok().body("{\"message\":\"Account deleted successfully\"}");
                } else {
                    return ResponseEntity.status(404).body("{\"error\":\"User not found\"}");
                }
            }
            return ResponseEntity.status(401).body("{\"error\":\"Unauthorized\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"error\":\"Failed to delete account: " + e.getMessage() + "\"}");
        }
    }
}
