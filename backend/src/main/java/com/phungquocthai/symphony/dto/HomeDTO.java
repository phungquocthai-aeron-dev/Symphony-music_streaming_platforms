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
public class HomeDTO {
	private List<SongDTO> hotHitSong;
    private List<SongDTO> vpopSongs;
    private List<SongDTO> usUkSongs;
    private List<SongDTO> remixSongs;
    private List<SongDTO> lyricalSongs;
    private List<SongDTO> newSongs;
    private List<SongDTO> recentlyListen;
    private List<SongDTO> recommend;
    private List<TopSongDTO> topSongs;
    private List<List<ListeningStatsDTO>> topTrendingStats;
}
