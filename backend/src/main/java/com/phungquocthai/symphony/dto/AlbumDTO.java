package com.phungquocthai.symphony.dto;

import java.time.LocalDate;
import com.phungquocthai.symphony.entity.Album;
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
public class AlbumDTO {
	
	@NotNull(message = "Id không được để trống")
    private Integer albumId;
    
    @NotNull(message = "Vui lòng chọn ngày phát hành")
    private LocalDate releaseDate;
    
    @NotNull(message = "Vui lòng chọn tên album")
    private String albumName;
    
    @NotNull(message = "Vui lòng chọn ảnh bìa album")
    private String albumImg;
    
    public AlbumDTO(Album album) {
    	this.albumId = album.getAlbumId();
    	this.albumImg = album.getAlbumImg();
    	this.releaseDate = album.getReleaseDate();
    	this.albumName = album.getAlbumName();
    }
}
