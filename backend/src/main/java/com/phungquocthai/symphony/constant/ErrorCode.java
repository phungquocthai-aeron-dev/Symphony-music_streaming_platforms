package com.phungquocthai.symphony.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {
	USER_EXISTED(1002, "Người dùng đã tồn tại"),
	USER_NOT_EXISRED(1005, "Người dùng không tồn tại"),
	VALIDATION(1004, "Lỗi xác thực dữ liệu"),
	UNAUTHENTICATED(1006, "Xác thực không thành công"),
	UNCATEGORIZED_EXCEPTION(9999, "Ngoại lệ chưa xác định"),
	FILE_NOT_FOUND(2001, "Không tìm thấy file"),
	FILE_UPLOAD_FAILED(2002, "Tải file lên thất bại"),
	FILE_TOO_LARGE(2003, "File vượt quá kích thước cho phép"),
	FILE_EXTENSION_NOT_ALLOWED(2004, "Định dạng file không hợp lệ"),
	FILE_STORAGE_FAILED(2005, "Lưu file thất bại"),
	FILE_DELETE_FAILED(2006, "Xóa file thất bại"),
	FILE_EMPTY(2007, "File tải lên không được rỗng"),
	FILE_READ_ERROR(2008, "Lỗi đọc file"),
	FILE_WRITE_ERROR(2009, "Lỗi ghi file"),
	FILE_ALREADY_EXISTS(2010, "File đã tồn tại"),
	INVALID_FILE_PATH(2011, "Đường dẫn file không hợp lệ"),
	DIRECTORY_CREATION_FAILED(2012, "Không thể tạo thư mục lưu trữ file"),
	FILE_DELETE_PERMISSION_DENIED(2013, "Không có quyền xóa file");
	
	private int code;
	private String message;
	
	private ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
