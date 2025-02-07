package com.phungquocthai.symphony.exception;

import com.phungquocthai.symphony.constant.ErrorCode;

public class AppException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private ErrorCode errorCode;
	
	public AppException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
	public ErrorCode getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
}
