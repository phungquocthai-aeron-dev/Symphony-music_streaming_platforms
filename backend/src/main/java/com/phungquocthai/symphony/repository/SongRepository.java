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
	
	@Query(value = "SELECT * FROM song WHERE release_date >= date_sub(CURDATE(), INTERVAL 1 YEAR) ORDER BY release_date DESC", 
	           nativeQuery = true)
	List<Song> findSongsFromLastYear();

	@Query(value = "SELECT DISTINCT s.song_id, s.song_name, s.song_img, s.listens, s.path, s.lyric, s.duration, s.release_date, s.author, s.category_id " +
            "FROM song s " +
            "NATURAL JOIN present p " +
            "NATURAL JOIN category c " +
            "WHERE s.song_name LIKE :key OR c.category_name LIKE :key " +
            "COLLATE utf8mb4_unicode_ci", nativeQuery = true)
	List<Song> searchSong(@Param("key") String key);
	
	@Query(value = "SELECT s " +
			"FROM song s " +
            "NATURAL JOIN present p " +
            "NATURAL JOIN category c " +
			"WHERE c.category_id = :categoryId", nativeQuery = true)
	List<Song> getSongsByCategory(@Param("categoryId") Integer categoryId);
	// TÌm tất cả ca sĩ theo bài hát
}
