package com.phungquocthai.symphony.dto;

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
public class SingerCreateDTO {
    
    @NotNull(message = "Vui lòng chọn nghệ danh")
    private String stageName;
    
    @NotNull(message = "Vui lòng chọn lượt theo dõi")
    @Builder.Default
    private int followers = 0;
   
}
