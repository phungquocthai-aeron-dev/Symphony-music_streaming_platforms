package com.phungquocthai.symphony.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.phungquocthai.symphony.entity.Favorite;
import com.phungquocthai.symphony.entity.Song;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.repository.FavoriteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteService {
	@Autowired
	FavoriteRepository favoriteRepository;
	
	public Favorite create(Integer user_id, Integer song_id) {
		Favorite favorite = Favorite.builder()
				.user(User.builder().userId(user_id).build())
				.song(Song.builder().song_id(song_id).build())
				.build();
		return favoriteRepository.save(favorite);
	}
}
