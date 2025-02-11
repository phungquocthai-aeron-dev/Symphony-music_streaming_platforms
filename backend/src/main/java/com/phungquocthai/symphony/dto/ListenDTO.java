package com.phungquocthai.symphony.dto;

import java.time.LocalDateTime;
import com.phungquocthai.symphony.entity.Listen;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListenDTO {
	@NotNull(message = "Id không được để trống")
	private int listen_id;
	
	@Column(name = "listen_at")
	@NotNull(message = "Vui lòng chọn thời gian nghe nhạc")
	private LocalDateTime listen_at;
	
	public ListenDTO(Listen listen) {
		this.listen_id = listen.getListen_id();
		this.listen_at = listen.getListen_at();
	}
}
