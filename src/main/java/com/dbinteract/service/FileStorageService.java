package com.dbinteract.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dbinteract.config.FileStorageConfig;

@Service
public class FileStorageService {
    
    private final FileStorageConfig fileConfig;
    
    public FileStorageService(FileStorageConfig fileConfig) {
        this.fileConfig = fileConfig;
    }
    
    public String getUploadDir() {
        return fileConfig.getUploadDir();
    }
    
    public String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Generate unique filename
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + "." + extension;
            
            Path uploadPath = Paths.get(fileConfig.getUploadDir()).toAbsolutePath().normalize();
            Path filePath = uploadPath.resolve(newFilename);
            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            return newFilename;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + originalFilename, e);
        }
    }
    
    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = Paths.get(fileConfig.getUploadDir())
                    .toAbsolutePath()
                    .normalize()
                    .resolve(filename);
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + filename, e);
        }
    }
    
    public void deleteFile(String filename) {
        try {
            Path filePath = Paths.get(fileConfig.getUploadDir())
                    .toAbsolutePath()
                    .normalize()
                    .resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + filename, e);
        }
    }
    
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1);
    }
}
