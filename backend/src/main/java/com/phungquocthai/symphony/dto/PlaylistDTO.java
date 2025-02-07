package com.phungquocthai.symphony.dto;

import java.time.LocalDate;
import com.phungquocthai.symphony.entity.Playlist;
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
public class PlaylistDTO {

	@NotNull(message = "Id không được để trống")
    private Integer playlistId;
    
    @NotNull(message = "Vui lòng chọn thời gian tạo playlist")
    private LocalDate createAt;
    
    @NotNull(message = "Vui lòng chọn tên playlist")
    private String playlistName;
    
    public PlaylistDTO(Playlist playlist) {
    	this.playlistId = playlist.getPlaylist_id();
    	this.createAt = playlist.getCreate_at();
    	this.playlistName = playlist.getPlaylist_name();
    }
}
