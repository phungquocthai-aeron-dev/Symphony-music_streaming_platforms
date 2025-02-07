package com.phungquocthai.symphony.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {
	USER_EXISTED(1002, "Người dùng đã tồn tại"),
	USER_NOT_EXISRED(1005, "Người dùng không tồn tại"),
	VALIDATION(1004, "Lỗi xác thực dữ liệu"),
	UNAUTHENTICATED(1006, "Xác thực không thành công"),
	UNCATEGORIZED_EXCEPTION(9999, "Ngoại lệ chưa xác định");
	
	private int code;
	private String message;
	
	private ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
