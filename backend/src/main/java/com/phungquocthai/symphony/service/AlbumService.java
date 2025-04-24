package com.phungquocthai.symphony.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.phungquocthai.symphony.constant.PathStorage;
import com.phungquocthai.symphony.dto.AlbumDTO;
import com.phungquocthai.symphony.dto.SongCreateDTO;
import com.phungquocthai.symphony.dto.SongDTO;
import com.phungquocthai.symphony.entity.Album;
import com.phungquocthai.symphony.entity.Singer;
import com.phungquocthai.symphony.entity.Song;
import com.phungquocthai.symphony.repository.AlbumRepository;
import com.phungquocthai.symphony.repository.SingerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SingerRepository singerRepository;
    
	@Autowired
	FileStorageService fileStorageService;

    public List<AlbumDTO> getAlbumsOfSinger(Integer singerId) {
        List<Album> albums = albumRepository.getBySingerId(singerId);
        return albums.stream().map(AlbumDTO::new).toList();
    }

    @PreAuthorize("hasRole('SINGER')")
    public AlbumDTO createAlbum(String albumName, Integer singerId, MultipartFile imgFile) {
        Singer singer = singerRepository.findById(singerId)
                .orElseThrow(() -> new RuntimeException("Singer not found"));

		String path = fileStorageService.storeFile(imgFile,PathStorage.ALBUM);
        Album album = Album.builder()
                .albumName(albumName)
                .albumImg(path)
                .singer(singer)
                .build();

        Album saved = albumRepository.save(album);
        return new AlbumDTO(saved);
    }

    @PreAuthorize("hasRole('SINGER')")
    public void updateAlbum(Integer albumId, String albumName, MultipartFile imgFile) {
    	Album entity = albumRepository.findById(albumId).orElseThrow();
    	String path = entity.getAlbumImg();
		if(imgFile != null) path = fileStorageService.storeFile(imgFile,PathStorage.ALBUM);
        entity.setAlbumImg(path);
        entity.setAlbumName(albumName);
        albumRepository.save(entity);
    }

    @Transactional
    @PreAuthorize("hasRole('SINGER')")
    public void deleteAlbumWithSongs(Integer albumId) {
        albumRepository.delete_album(albumId);
    }

    public boolean isSongInAlbum(Integer albumId, Integer songId) {
        Integer result = albumRepository.isSongInAlbum(albumId, songId);
        return result != null;
    }

    @PreAuthorize("hasRole('SINGER')")
    public void addSongToAlbum(Integer albumId, Integer songId) {
        albumRepository.addSongToAlbum(albumId, songId);
    }

    @PreAuthorize("hasRole('SINGER')")
    public void removeSongFromAlbum(Integer albumId, Integer songId) {
        albumRepository.removeSongFromAlbum(albumId, songId);
    }
}
