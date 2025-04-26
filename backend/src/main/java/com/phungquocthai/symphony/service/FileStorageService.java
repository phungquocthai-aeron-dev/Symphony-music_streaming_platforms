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
	private final String baseStoragePath = "D:\\nienluan\\Symphony-music-streaming-platforms\\backend\\uploads";
	
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

	    public void deleteFile(String absoluteFilePath) {
	        try {
	            // Chuyển chuỗi đường dẫn tuyệt đối thành đối tượng Path
	            Path filePath = Paths.get(absoluteFilePath).toAbsolutePath().normalize();
	            
	            // Kiểm tra xem đường dẫn tệp có hợp lệ và nằm trong phạm vi thư mục cho phép (ví dụ: "D:/uploads/")
	            Path basePath = Paths.get("D:\\nienluan\\Symphony-music-streaming-platforms\\backend\\uploads").toAbsolutePath().normalize();
	            if (!filePath.startsWith(basePath)) {
	                throw new AppException(ErrorCode.FILE_DELETE_PERMISSION_DENIED);
	            }

	            // Kiểm tra xem tệp có tồn tại không, nếu có thì xóa
	            if (Files.exists(filePath)) {
	                Files.delete(filePath);
	                log.info("File deleted successfully: {}", absoluteFilePath);
	            } else {
	                throw new AppException(ErrorCode.FILE_NOT_FOUND);
	            }
	        } catch (IOException ex) {
	            log.error("Failed to delete file: {}", absoluteFilePath, ex);
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