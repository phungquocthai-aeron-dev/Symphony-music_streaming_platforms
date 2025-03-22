package com.phungquocthai.symphony.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.constant.ErrorCode;
import com.phungquocthai.symphony.constant.PathStorage;
import com.phungquocthai.symphony.exception.AppException;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {
	private final String baseStoragePath = "D:\\nienluan\\Symphony-music-streaming-platforms\\backend\\src\\main\\resources\\static";
	
	    public String storeFile(MultipartFile file, PathStorage pathStorage) {
	    	log.info("X");
	        log.info("Starting to store file: {}", file.getOriginalFilename());

	        Path fileStorageLocation = initializeStorageLocation(pathStorage);
	        try {
	            String fileName = UUID.randomUUID().toString() + 
	                            getFileExtension(file.getOriginalFilename());
	            log.info("Y");
	            Path targetLocation = fileStorageLocation.resolve(fileName);
	            Files.copy(file.getInputStream(), targetLocation);
	            log.info("Z");
	            return pathStorage.getPath() + fileName;
	        } catch (Exception ex) {
	        	log.info("T");
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
	    	String fullPath = baseStoragePath + pathStorage.getPath();
	        log.info("Base path: {}", baseStoragePath);
	        log.info("Sub path: {}", pathStorage.getPath());
	        log.info("Full path to create: {}", fullPath);
	    	
	        Path location = Paths.get(baseStoragePath + pathStorage.getPath())
	            .toAbsolutePath().normalize();
	        log.info("File storage location: {}", location);
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