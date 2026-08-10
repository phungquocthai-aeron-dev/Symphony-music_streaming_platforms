package com.phungquocthai.symphony.service;

import com.phungquocthai.symphony.constant.PathStorage;
import com.phungquocthai.symphony.dto.NotificationDTO;
import com.phungquocthai.symphony.dto.SongCreateDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.entity.Song;
import com.phungquocthai.symphony.mapper.SongCreateMapper;
import com.phungquocthai.symphony.mapper.SongMapper;
import com.phungquocthai.symphony.repository.SongRepository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class SongServiceSpringTest {
    @Autowired
    SongService songService;

    @MockBean
    SongRepository songRepository;

    @MockBean
    NotificationService notificationService;

    @MockBean
    AISearchService aiSearchService;

    @MockBean
    SongCreateMapper songCreateMapper;

    @MockBean
    SongMapper songMapper;

    @MockBean
    FileStorageService fileStorageService;

//    Happy case
    private SongCreateDTO songCreateDTO;
    private Song song;
    private MultipartFile pathFile;
    private MultipartFile lyricFile;
    private MultipartFile lrcFile;
    private MultipartFile songImgFile;

    @BeforeEach
    void initData() {
        pathFile = Mockito.mock(MultipartFile.class);
        lyricFile = Mockito.mock(MultipartFile.class);
        lrcFile = Mockito.mock(MultipartFile.class);
        songImgFile = Mockito.mock(MultipartFile.class);

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

        this.song = Song.builder()
                .song_id(1)
                .songName("About Java")
                .author("Phung Quoc Thai")
                .lrc(null)
                .lyric(null)
                .song_img(null)
                .total_listens(0)
                .path(null)
                .isVip(false)
                .duration(100)
                .releaseDate(LocalDate.now().plusDays(7))
                .active(true)
                .build();
    }

    @Test
    void createSong_valid_success() {

        // GIVEN
        Mockito.when(songCreateMapper.toEntity(songCreateDTO))
                .thenReturn(song);

        Mockito.when(fileStorageService.storeFile(
                        pathFile,
                        PathStorage.MUSIC_NORMAL))
                .thenReturn("music/test.mp3");

        Mockito.when(fileStorageService.storeFile(
                        lyricFile,
                        PathStorage.LYRIC))
                .thenReturn("lyrics/test.txt");

        Mockito.when(fileStorageService.storeFile(
                        lrcFile,
                        PathStorage.LRC))
                .thenReturn("lrc/test.lrc");

        Mockito.when(fileStorageService.storeFile(
                        songImgFile,
                        PathStorage.MUSIC_IMG))
                .thenReturn("images/test.jpg");

        Mockito.when(songRepository.save(Mockito.any(Song.class)))
                .thenReturn(song);

        Mockito.when(songMapper.toDTO(Mockito.any(Song.class)))
                .thenReturn(
                        SongDTO.builder()
                                .song_id(1)
                                .songName("About Java")
                                .author("Phung Quoc Thai")
                                .duration(100)
                                .isVip(false)
                                .build()
                );

        Mockito.when(songRepository.addSongToSinger(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(1);

        Mockito.when(songRepository.addSongToCategory(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(1);

        Mockito.when(notificationService.sendNotificationToAllUsers(
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyString()))
                .thenReturn(NotificationDTO.builder().notificationId(1).build());

        // WHEN
        SongDTO response =
                songService.create(songCreateDTO, pathFile, lyricFile, lrcFile, songImgFile);

        // THEN
        Assertions.assertThat(response.getSong_id()).isEqualTo(1);
        Assertions.assertThat(response.getSongName()).isEqualTo("About Java");
        Assertions.assertThat(response.getAuthor()).isEqualTo("Phung Quoc Thai");

        Mockito.verify(songRepository).save(Mockito.any(Song.class));
        Mockito.verify(songRepository).addSongToSinger(3, 1);
        Mockito.verify(songRepository).addSongToSinger(5, 1);
        Mockito.verify(songRepository).addSongToCategory(1, 1);
        Mockito.verify(songRepository).addSongToCategory(2, 1);

        Mockito.verify(notificationService)
                .sendNotificationToAllUsers(
                        Mockito.eq(1),
                        Mockito.anyString(),
                        Mockito.eq("Bài hát mới"));

        Mockito.verify(aiSearchService)
                .updateAiData("music/test.mp3");

        Mockito.verify(fileStorageService)
                .storeFile(pathFile, PathStorage.MUSIC_NORMAL);
        Mockito.verify(fileStorageService)
                .storeFile(lyricFile, PathStorage.LYRIC);
        Mockito.verify(fileStorageService)
                .storeFile(lrcFile, PathStorage.LRC);
        Mockito.verify(fileStorageService)
                .storeFile(songImgFile, PathStorage.MUSIC_IMG);

        ArgumentCaptor<Song> captor = ArgumentCaptor.forClass(Song.class);
        Mockito.verify(songRepository).save(captor.capture());

        Song savedSong = captor.getValue();

        Assertions.assertThat(savedSong.getPath()).isEqualTo("music/test.mp3");
        Assertions.assertThat(savedSong.getLyric()).isEqualTo("lyrics/test.txt");
        Assertions.assertThat(savedSong.getLrc()).isEqualTo("lrc/test.lrc");
        Assertions.assertThat(savedSong.getSong_img()).isEqualTo("images/test.jpg");
        Assertions.assertThat(savedSong.isActive()).isTrue();
    }

    @Test
    void createSong_vip_success() {

        songCreateDTO.setIsVip(true);

        Mockito.when(songCreateMapper.toEntity(songCreateDTO))
                .thenReturn(song);

        Mockito.when(fileStorageService.storeFile(
                pathFile,
                PathStorage.MUSIC_VIP))
                .thenReturn("music/vip/song.mp3");

        Mockito.when(fileStorageService.storeFile(
                        pathFile,
                        PathStorage.MUSIC_NORMAL))
                .thenReturn("music/test.mp3");

        Mockito.when(fileStorageService.storeFile(
                        lyricFile,
                        PathStorage.LYRIC))
                .thenReturn("lyrics/test.txt");

        Mockito.when(fileStorageService.storeFile(
                        lrcFile,
                        PathStorage.LRC))
                .thenReturn("lrc/test.lrc");

        Mockito.when(fileStorageService.storeFile(
                        songImgFile,
                        PathStorage.MUSIC_IMG))
                .thenReturn("images/test.jpg");

        Mockito.when(songRepository.save(Mockito.any(Song.class)))
                .thenReturn(song);

        Mockito.when(songMapper.toDTO(Mockito.any(Song.class)))
                .thenReturn(
                        SongDTO.builder()
                                .song_id(1)
                                .songName("About Java")
                                .author("Phung Quoc Thai")
                                .duration(100)
                                .isVip(false)
                                .build()
                );

        Mockito.when(songRepository.addSongToSinger(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(1);

        Mockito.when(songRepository.addSongToCategory(Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(1);

        Mockito.when(notificationService.sendNotificationToAllUsers(
                        Mockito.anyInt(),
                        Mockito.anyString(),
                        Mockito.anyString()))
                .thenReturn(NotificationDTO.builder().notificationId(1).build());

        songService.create(
                songCreateDTO,
                pathFile,
                lyricFile,
                lrcFile,
                songImgFile
        );

        Mockito.verify(fileStorageService)
                .storeFile(pathFile, PathStorage.MUSIC_VIP);

        Mockito.verify(fileStorageService, Mockito.never())
                .storeFile(pathFile, PathStorage.MUSIC_NORMAL);
    }

}
