package com.phungquocthai.symphony.dto;

import java.util.List;
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
public class LibraryDTO {
	private List<SongDTO> songs;
	private List<PlaylistDTO> playlists;
}
