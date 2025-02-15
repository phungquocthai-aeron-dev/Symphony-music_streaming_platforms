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
public class RankingDTO {
	private List<TopSongDTO> topSong;
	private List<List<ListeningStatsDTO>> top3;
}
