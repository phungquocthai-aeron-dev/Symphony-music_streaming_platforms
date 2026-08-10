package com.phungquocthai.symphony.controller;

import com.phungquocthai.symphony.dto.SongCreateDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.service.SongServiceCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(SongControllerCache.class)
public class SongControllerSpringTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SongServiceCache songServiceCache;

    private SongCreateDTO songCreateDTO;

    @BeforeEach
    void initData() {
        this.songCreateDTO = SongCreateDTO.builder()
                .author("Phung Quoc Thai")
                .duration(100)
                .total_listens(0)
                .releaseDate(LocalDate.now().plusDays(7))
                .categoryIds(List.of(1, 2))
                .singersId(List.of(3, 5))
                .songName("About Java")
                .isVip(false)
                .build();
    }

    @Test
    void create_success() throws Exception {

        SongDTO response = SongDTO.builder()
                .song_id(1)
                .songName("About Java")
                .author("Phung Quoc Thai")
                .duration(100)
                .isVip(false)
                .build();

        Mockito.when(songServiceCache.create(
                Mockito.any(SongCreateDTO.class),
                Mockito.any(MultipartFile.class),
                Mockito.any(MultipartFile.class),
                Mockito.any(MultipartFile.class),
                Mockito.any(MultipartFile.class)
        )).thenReturn(response);

        MockMultipartFile musicFile =
                new MockMultipartFile(
                        "musicFile",
                        "about-java.mp3",
                        "audio/mpeg",
                        "fake music".getBytes()
                );

        MockMultipartFile lrcFile =
                new MockMultipartFile(
                        "lrcFile",
                        "about-java.lrc",
                        "text/plain",
                        "fake lrc".getBytes()
                );

        MockMultipartFile lyricFile =
                new MockMultipartFile(
                        "lyricFile",
                        "about-java.txt",
                        "text/plain",
                        "fake lyric".getBytes()
                );

        MockMultipartFile songImgFile =
                new MockMultipartFile(
                        "songImgFile",
                        "about-java.jpg",
                        "image/jpeg",
                        "fake image".getBytes()
                );

        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/song/create")
                                .file(musicFile)
                                .file(lrcFile)
                                .file(lyricFile)
                                .file(songImgFile)
                                .param("author", "Phung Quoc Thai")
                                .param("duration", "100")
                                .param("total_listens", "0")
                                .param("releaseDate", songCreateDTO.getReleaseDate().toString())
                                .param("songName", "About Java")
                                .param("isVip", "false")
                                .param("categoryIds", "1", "2")
                                .param("singersId", "3", "5")

                                .with(user("testuser").roles("SINGER"))
                                .with(csrf())
                )
                .andExpect(status().isOk());

        Mockito.verify(songServiceCache).create(
                Mockito.any(SongCreateDTO.class),
                Mockito.any(MultipartFile.class),
                Mockito.any(MultipartFile.class),
                Mockito.any(MultipartFile.class),
                Mockito.any(MultipartFile.class)
        );
    }
}
