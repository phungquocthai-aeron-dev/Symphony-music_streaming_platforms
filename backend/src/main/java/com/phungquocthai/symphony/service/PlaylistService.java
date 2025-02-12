package com.phungquocthai.symphony.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.phungquocthai.symphony.dto.PlaylistDTO;
import com.phungquocthai.symphony.entity.Playlist;
import com.phungquocthai.symphony.mapper.PlaylistMapper;
import com.phungquocthai.symphony.repository.PlaylistRepository;

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
	PlaylistMapper playlistMapper;
	
	public List<PlaylistDTO> getPlaylistOfUser(Integer userId) {
		List<Playlist> entities = playlistRepository.getByUserId(userId);
		return playlistMapper.toListDTO(entities);
	}
}
