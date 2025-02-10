package com.phungquocthai.symphony.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationDTO {
    @NotBlank(message = "Vui lòng điền họ và tên")
    @Size(max = 50, message = "Họ và tên không được vượt quá 50 ký tự")
    private String fullName;

    @NotNull(message = "Vui lòng nhập ngày sinh")
    private LocalDate birthday;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 ký tự")
    private String password;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "^(03|05|07|08|09|01[2|6|8|9])+([0-9]{8})\\b$", message = "Số điện thoại không hợp lệ")
    private String phone;

    @NotNull(message = "Vui lòng chọn giới tính")
    private Integer gender;
    
    @Builder.Default
    private String avatar = "/images/avatars/default-avatar.jpg";

}
