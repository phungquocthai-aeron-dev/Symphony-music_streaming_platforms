package com.phungquocthai.symphony.controller;

import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.dto.SongCreateDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.service.SongServiceCache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongControllerTest {

    @Mock
    private SongServiceCache songService;

    @InjectMocks
    private SongControllerCache controller;

    private SongCreateDTO songCreateDTO;

    private MultipartFile musicFile;
    private MultipartFile lrcFile;
    private MultipartFile lyricFile;
    private MultipartFile songImgFile;

    @BeforeEach
    void initData() {

        songCreateDTO = SongCreateDTO.builder()
                .author("Phung Quoc Thai")
                .duration(100)
                .total_listens(0)
                .releaseDate(LocalDate.now().plusDays(7))
                .categoryIds(List.of(1, 2))
                .singersId(List.of(3, 5))
                .songName("About Java")
                .isVip(false)
                .build();

        musicFile = mock(MultipartFile.class);
        lrcFile = mock(MultipartFile.class);
        lyricFile = mock(MultipartFile.class);
        songImgFile = mock(MultipartFile.class);
    }

    @Test
    void create_success() {

        // GIVEN
        SongDTO response = SongDTO.builder()
                .song_id(1)
                .songName("About Java")
                .author("Phung Quoc Thai")
                .duration(100)
                .isVip(false)
                .build();

        when(songService.create(
                any(SongCreateDTO.class),
                any(MultipartFile.class),
                any(MultipartFile.class),
                any(MultipartFile.class),
                any(MultipartFile.class)
        )).thenReturn(response);

        // WHEN
        ResponseEntity<ApiResponse<SongDTO>> result =
                controller.create(
                        songCreateDTO,
                        musicFile,
                        lrcFile,
                        lyricFile,
                        songImgFile
                );

        // THEN
        assertThat(result.getStatusCode().value())
                .isEqualTo(200);

        assertThat(result.getBody()).isNotNull();

        assertThat(result.getBody().getResult().getSong_id())
                .isEqualTo(1);

        assertThat(result.getBody().getResult().getSongName())
                .isEqualTo("About Java");

        assertThat(result.getBody().getResult().getAuthor())
                .isEqualTo("Phung Quoc Thai");

        verify(songService).create(
                songCreateDTO,
                musicFile,
                lyricFile,
                lrcFile,
                songImgFile
        );
    }
}