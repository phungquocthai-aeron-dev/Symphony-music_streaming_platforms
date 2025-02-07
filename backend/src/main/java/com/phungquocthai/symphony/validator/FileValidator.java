package com.phungquocthai.symphony.validator;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.phungquocthai.symphony.annotation.ValidFile;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
	private long maxSize;
    private String[] allowedContentTypes;
    
    
	@Override
	public void initialize(ValidFile constraintAnnotation) {
		this.maxSize = constraintAnnotation.maxSize();
        this.allowedContentTypes = constraintAnnotation.allowedContentTypes();
	}


	@Override
	public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
		 if (file == null || file.isEmpty()) {
	            return false;
	        }

	        // Kiểm tra kích thước file
	        if (file.getSize() > this.maxSize) {
	            return false;
	        }

	        // Kiểm tra loại file
	        String contentType = file.getContentType();
	        boolean isAllowedType = false;
	        for (String allowedType : this.allowedContentTypes) {
	            if (allowedType.equals(contentType)) {
	                isAllowedType = true;
	                break;
	            }
	        }

	        return isAllowedType;
	}
	
	
    
}