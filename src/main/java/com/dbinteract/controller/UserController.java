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
    public ResponseEntity<Void> deleteMyAccount() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserPrincipal) {
                int userId = ((UserPrincipal) principal).getUserId();
                boolean deleted = userDAO.deleteById(userId);
                if (deleted) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            }
            return ResponseEntity.status(401).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
