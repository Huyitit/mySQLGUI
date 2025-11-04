package com.dbinteract.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.dbinteract.dao.UserDAO;
import com.dbinteract.dto.AuthResponse;
import com.dbinteract.dto.LoginRequest;
import com.dbinteract.dto.RegisterRequest;
import com.dbinteract.models.User;
import com.dbinteract.security.JwtTokenProvider;
import com.dbinteract.utils.PasswordHasher;

@Service
public class AuthService {
    
    private final UserDAO userDAO;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    public AuthService(UserDAO userDAO, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userDAO = userDAO;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }
    
    public AuthResponse register(RegisterRequest request) {
        try {
            // Check if username exists
            User existing = userDAO.findByUsername(request.getUsername());
            if (existing != null) {
                throw new RuntimeException("Username already exists!");
            }
            
            // Create new user
            User user = new User();
            user.setUserName(request.getUsername());
            user.setHashedPassword(PasswordHasher.hashPassword(request.getPassword()));
            
            int userId = userDAO.insert(user);
            user.setUserId(userId);
            
            // Return null token - user must login manually
            return new AuthResponse(null, user.getUserId(), user.getUserName());
        } catch (Exception e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);
            
            User user = userDAO.findByUsername(request.getUsername());
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            
            return new AuthResponse(token, user.getUserId(), user.getUserName());
        } catch (Exception e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
    }
}
