package com.phungquocthai.symphony.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.phungquocthai.symphony.dto.PlaylistDTO;
import com.phungquocthai.symphony.entity.Playlist;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.repository.PlaylistRepository;
import com.phungquocthai.symphony.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PlaylistService {
	@Autowired
	PlaylistRepository playlistRepository;
	
	@Autowired
    UserRepository userRepository;

	public List<PlaylistDTO> getPlaylistOfUser(Integer userId) {
	    List<Playlist> entities = playlistRepository.getByUserId(userId);
	    
	    return entities.stream()
	                   .map(PlaylistDTO::new)
	                   .toList();
	}

	public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Playlist playlist = Playlist.builder()
                .playlist_name(playlistDTO.getPlaylistName())
                .user(user)
                .build();

        Playlist saved = playlistRepository.save(playlist);

        return PlaylistDTO.builder()
                .playlistId(saved.getPlaylist_id())
                .playlistName(saved.getPlaylist_name())
                .createAt(saved.getCreate_at())
                .build();
    }
	
	public void update(Integer id, String playlistnName) {
		Playlist playlist = playlistRepository.findById(id).orElseThrow();
		playlist.setPlaylist_name(playlistnName);
		playlistRepository.save(playlist);
	}
	
	@Transactional
	public void deletePlaylistWithSongs(Integer playlistId) {
        playlistRepository.delete_playlist(playlistId);
    }
	
	public boolean isSongInPlaylist(Integer playlistId, Integer songId) {
		Integer playlist_id = playlistRepository.isSongInPlaylist(playlistId, songId);
		if(playlist_id == null) return false;
        return true;
    }

    public void addSongToPlaylist(Integer playlistId, Integer songId) {
        playlistRepository.addSongToPlaylist(playlistId, songId);
    }

    public void removeSongFromPlaylist(Integer playlistId, Integer songId) {
        playlistRepository.removeSongFromPlaylist(playlistId, songId);
    }
}
