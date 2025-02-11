package com.phungquocthai.symphony.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {
	
	@NotNull(message = "Id không được để trống")
	private Integer id;
	
	@NotBlank(message = "Vui lòng điền họ và tên")
	private String fullName;
	
	@NotNull(message = "Vui lòng nhập ngày sinh")
	private LocalDate birthday;
	
	@NotBlank(message = "Vui lòng nhập số điện thoại")
	@Pattern(regexp = "^(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})\\b$/", message = "Số điện thoại không hợp lệ")
	private String phone;
	
	@NotNull(message = "Vui lòng chọn giới tính")
	private int gender;
		
	@NotNull(message = "Vui lòng xác thực quyền")
	private String role;

}
