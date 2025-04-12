package com.phungquocthai.symphony.dto;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.Min;
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
public class TopSongDTO {

    @NotNull(message = "Id không được để trống")
    private Integer song_id;
    
    @NotNull(message = "Vui lòng nhập tên bài hát")
    private String songName;
    
    @NotNull(message = "Vui lòng chọn ảnh bài hát")
    private String song_img;
    
    @NotNull(message = "Vui lòng chọn lượt nghe")
    @Min(value = 0, message = "Lượt nghe phải lớn hơn 0")
    private Integer total_listens;
    
    @NotNull(message = "Vui lòng chọn file bài hát")
    private String path;
    
    @NotNull(message = "Vui lòng chọn file lyric")
    private String lyric;
    
    @NotNull(message = "Vui lòng chọn file lrc")
    private String lrc;
    
    @NotNull(message = "Vui lòng chọn thời lượng bài hát")
    private Integer duration;
    
    @NotNull(message = "Vui lòng chọn ngày phát hành")
    private LocalDate releaseDate;
    
    @NotNull(message = "Vui lòng chọn tên tác giả")
    private String author;
    
    @NotNull(message = "Vui lòng chọn đặc quyền bài hát")
    private Boolean isVip;
    
    private Long total_listens_per_hour;
    
    @NotEmpty(message = "Vui lòng chọn thể loại cho bài hát")
    private List<Integer> categoriesId;
    
    @NotEmpty(message = "Vui lòng chọn ca sĩ thể hiện")
    private List<SingerDTO> singers;
    
    private boolean favorite;
}
