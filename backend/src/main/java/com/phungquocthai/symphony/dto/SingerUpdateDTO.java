package com.phungquocthai.symphony.dto;

import jakarta.validation.constraints.Min;
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
public class SingerUpdateDTO {
	@NotNull(message = "Id không được để trống")
    @Min(value = 1, message = "Id phải lớn hơn hoặc bằng 1")
	private Integer singer_id;
    
    @NotNull(message = "Vui lòng chọn nghệ danh")
    private String stageName;
    
    @NotNull(message = "Vui lòng chọn lượt theo dõi")
    @Builder.Default
    private int followers = 0;
   
}
