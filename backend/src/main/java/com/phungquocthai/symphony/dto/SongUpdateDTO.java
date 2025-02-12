package com.phungquocthai.symphony.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;
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
public class SongUpdateDTO {

    @NotNull(message = "Id không được để trống")
    private Integer song_id;
    
    @NotNull(message = "Vui lòng nhập tên bài hát")
    private String songName;
    
    @NotNull(message = "Vui lòng chọn thời lượng bài hát")
    private Integer duration;
    
    @NotNull(message = "Vui lòng chọn tên tác giả")
    private String author;
    
    @NotNull(message = "Vui lòng chọn đặc quyền bài hát")
    private Boolean isVip;
    
    @NotEmpty(message = "Vui lòng chọn thể loại cho bài hát")
    private List<Integer> categoriesId;
    
    private List<Integer> singersId;
    
}
