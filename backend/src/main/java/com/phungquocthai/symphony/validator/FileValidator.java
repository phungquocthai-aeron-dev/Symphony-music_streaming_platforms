package com.phungquocthai.symphony.validator;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.phungquocthai.symphony.annotation.ValidFile;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    private long maxSize;
    private String[] allowedContentTypes;
    private boolean required;
    
    @Override
    public void initialize(ValidFile constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedContentTypes = constraintAnnotation.allowedContentTypes();
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (!required && (file == null || file.isEmpty())) {
            return true;
        }
    	
        if (file == null || file.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("File không được để trống")
                  .addConstraintViolation();
            return false;
        }

        // Kiểm tra kích thước file
        if (file.getSize() > this.maxSize) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Kích thước file không được vượt quá " + (this.maxSize / 1024 / 1024) + "MB")
                  .addConstraintViolation();
            return false;
        }

        // Kiểm tra loại file
        String contentType = file.getContentType();
        if (contentType == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Không thể xác định loại file")
                  .addConstraintViolation();
            return false;
        }

        boolean isAllowedType = false;
        for (String allowedType : this.allowedContentTypes) {
            if (allowedType.equals(contentType)) {
                isAllowedType = true;
                break;
            }
        }

        if (!isAllowedType) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
            		"Loại file không được hỗ trợ. Chỉ chấp nhận: " + String.join(", ", allowedContentTypes))
                  .addConstraintViolation();
        }

        return isAllowedType;
    }
}