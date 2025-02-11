package com.phungquocthai.symphony.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.constant.ErrorCode;
import com.phungquocthai.symphony.constant.PathStorage;
import com.phungquocthai.symphony.exception.AppException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
	    private final String baseStoragePath = "static";
	    
	    public String storeFile(MultipartFile file, PathStorage pathStorage) {
	        Path fileStorageLocation = initializeStorageLocation(pathStorage);
	        try {
	            String fileName = UUID.randomUUID().toString() + 
	                            getFileExtension(file.getOriginalFilename());
	            
	            Path targetLocation = fileStorageLocation.resolve(fileName);
	            Files.copy(file.getInputStream(), targetLocation);
	            
	            return pathStorage.getPath() + fileName;
	        } catch (Exception ex) {
	            throw new AppException(ErrorCode.FILE_STORAGE_FAILED);
	        }
	    }
	    
	    public Path loadFile(String fileName, PathStorage pathStorage) {
	        Path fileStorageLocation = initializeStorageLocation(pathStorage);
	        Path filePath = fileStorageLocation.resolve(fileName).normalize();
	        if (!Files.exists(filePath)) {
	            throw new AppException(ErrorCode.FILE_NOT_FOUND);
	        }
	        return filePath;
	    }

	    public void deleteFile(String fileName, PathStorage pathStorage) {
	        try {
	            Path fileStorageLocation = initializeStorageLocation(pathStorage);
	            Path filePath = fileStorageLocation.resolve(fileName).normalize();
	            if (!filePath.startsWith(fileStorageLocation)) {
	                throw new AppException(ErrorCode.FILE_DELETE_PERMISSION_DENIED);
	            }
	            Files.deleteIfExists(filePath);
	        } catch (IOException ex) {
	            throw new AppException(ErrorCode.FILE_DELETE_FAILED);
	        }
	    }

	    private Path initializeStorageLocation(PathStorage pathStorage) {
	        Path location = Paths.get(baseStoragePath + pathStorage.getPath())
	            .toAbsolutePath().normalize();
	        try {
	            Files.createDirectories(location);
	        } catch (Exception ex) {
	            throw new AppException(ErrorCode.DIRECTORY_CREATION_FAILED);
	        }
	        return location;
	    }
	    
	    private String getFileExtension(String fileName) {
	        if (fileName == null) return "";
	        int lastDotIndex = fileName.lastIndexOf('.');
	        return lastDotIndex == -1 ? "" : fileName.substring(lastDotIndex);
	    }
}