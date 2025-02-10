package com.phungquocthai.symphony.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import com.phungquocthai.symphony.entity.Category;
import com.phungquocthai.symphony.entity.Singer;
import com.phungquocthai.symphony.entity.Song;
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
public class SongCreateDTO {

    @NotNull(message = "Id không được để trống")
    private Integer song_id;
    
    @NotNull(message = "Vui lòng nhập tên bài hát")
    private String songName;
    
    @NotNull(message = "Vui lòng chọn ảnh bài hát")
    private String song_img;
    
    @NotNull(message = "Vui lòng chọn lượt nghe")
    @Min(value = 0, message = "Lượt nghe phải lớn hơn hoặc bằng 0")
    @Builder.Default
    private Integer total_listens = 0;
    
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
    
    @NotEmpty(message = "Vui lòng chọn thể loại cho bài hát")
    private Set<Integer> categoriesId;
    
    @NotEmpty(message = "Vui lòng chọn ca sĩ thể hiện")
    private Set<Integer> singersId;
        
    public SongCreateDTO(Song song) {
    	this.song_id = song.getSong_id();
    	this.author = song.getAuthor();
    	this.duration = song.getDuration();
    	this.isVip = song.getIsVip();
    	this.lrc = song.getLrc();
    	this.lyric = song.getLyric();
    	this.path = song.getPath();
    	this.releaseDate = song.getReleaseDate();
    	this.song_img = song.getSong_img();
    	this.total_listens = song.getTotal_listens();
    	
    	if(song.getCategories() != null) {
    		this.categoriesId = song.getCategories().stream().map(Category::getCategory_id).collect(Collectors.toSet());
    	}
    	else {
    		this.categoriesId = null;
    	}
    	
    	if(song.getSingers() != null) {
    		this.singersId = song.getSingers().stream().map(Singer::getSinger_id).collect(Collectors.toSet());
    	}
    	else {
    		this.singersId = null;
    	}
    }

}
