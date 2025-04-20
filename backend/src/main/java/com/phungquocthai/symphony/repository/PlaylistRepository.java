package com.phungquocthai.symphony.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.phungquocthai.symphony.entity.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
	
	@Query("SELECT p FROM Playlist p WHERE p.user.userId = :userId")
	List<Playlist> getByUserId(@Param("userId") Integer userId);
	
	@Modifying
	@Procedure(name = "delete_playlist")
    void delete_playlist(@Param("p_playlist_id") Integer playlistId);
	
	@Modifying
    @Transactional
    @Query(value = "INSERT INTO playlist_song (playlist_id, song_id) VALUES (:playlistId, :songId)", 
           nativeQuery = true)
    void addSongToPlaylist(@Param("playlistId") Integer playlistId, @Param("songId") Integer songId);
	

	@Query(value = "SELECT playlist_id FROM playlist_song WHERE playlist_id = :playlistId AND song_id = :songId", 
		       nativeQuery = true)
	Integer isSongInPlaylist(@Param("playlistId") Integer playlistId, @Param("songId") Integer songId);
	
	@Modifying
    @Transactional
    @Query(value = "DELETE FROM playlist_song WHERE playlist_id = :playlistId AND song_id = :songId", 
           nativeQuery = true)
    void removeSongFromPlaylist(@Param("playlistId") Integer playlistId, @Param("songId") Integer songId);
}
