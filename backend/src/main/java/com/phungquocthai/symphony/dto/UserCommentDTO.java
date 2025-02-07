package com.phungquocthai.symphony.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.phungquocthai.symphony.entity.User;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCommentDTO {
	@NotNull(message = "Id không được để trống")
	private Integer userId;
	
	@NotNull(message = "Vui lòng điền họ và tên")
	private String fullName;
	
	public UserCommentDTO(User user) {
		this.userId = user.getUserId();
		this.fullName = user.getFullName();
	}
}
