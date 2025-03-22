package com.phungquocthai.symphony.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationRequest {
	@NotBlank(message = "Vui lòng nhập số điện thoại")
//    @Pattern(regexp = "^(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})\\b$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    private String password;
}
