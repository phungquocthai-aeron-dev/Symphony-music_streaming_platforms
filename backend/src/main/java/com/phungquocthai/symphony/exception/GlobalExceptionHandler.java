package com.phungquocthai.symphony.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.phungquocthai.symphony.constant.ErrorCode;
import com.phungquocthai.symphony.dto.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
    	ApiResponse<Object> apiResponse = new ApiResponse<Object>();
    	apiResponse.setCode(ErrorCode.VALIDATION.getCode());
    	apiResponse.setMessage(ErrorCode.VALIDATION.getMessage());
    	apiResponse.setResult(errors);
    	
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleAppException(AppException ex) {
    	ErrorCode errorCode = ex.getErrorCode();
    	ApiResponse<Object> apiResponse = new ApiResponse<Object>();
    	apiResponse.setCode(errorCode.getCode());
    	apiResponse.setMessage(errorCode.getMessage());
    	
    	return ResponseEntity.badRequest().body(apiResponse);
    }
    
}
