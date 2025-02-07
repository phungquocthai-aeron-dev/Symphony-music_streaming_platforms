package com.phungquocthai.symphony.dto;

import com.phungquocthai.symphony.entity.Singer;
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
public class SingerDTO {

    @NotNull(message = "Id không được để trống")
    private Integer singerId;
    
    @NotNull(message = "Vui lòng chọn nghệ danh")
    private String stageName;
    
    @NotNull(message = "Vui lòng chọn lượt theo dõi")
    @Min(value = 0, message = "Lượt theo dõi phải lớn hơn 0")
    private int followers;
   
    public SingerDTO(Singer singer) {
    	this.singerId = singer.getSinger_id();
    	this.stageName = singer.getStage_name();
    	this.followers = singer.getFollowers();
    }
}
