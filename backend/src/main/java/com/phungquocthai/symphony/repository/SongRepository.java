package com.phungquocthai.symphony.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.phungquocthai.symphony.entity.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
	@Query("SELECT s FROM Song s JOIN s.favorites f WHERE f.user.userId = :userId")
	List<Song> getFavoriteSongsOfUser(@Param("userId") Integer userId);
	
	@Query("SELECT s, MAX(l.listen_at) as lastListenAt "
			+ "FROM Song s JOIN s.listens l "
			+ "WHERE l.user.userId = :userId "
			+ "GROUP BY s.song_id "
			+ "ORDER BY lastListenAt DESC")
	List<Song> getRecentlyListenSongs(@Param("userId") Integer userId);
	
	@Modifying
    @Transactional
	@Query(value =  "INSERT INTO present (singer_id, song_id) VALUES (:singerId, :songId)", nativeQuery = true)
    int addSongToSinger(@Param("singerId") Integer singerId, @Param("songId") Integer songId);
	
	// TÌm tất cả ca sĩ theo bài hát
}
