package com.phungquocthai.symphony.dto;

import java.time.LocalDate;

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
public class ListeningStatsDTO {
    private Integer song_id;
    private LocalDate listen_date;
    private Integer hour;
    private Long total_listens_per_hour;
}
