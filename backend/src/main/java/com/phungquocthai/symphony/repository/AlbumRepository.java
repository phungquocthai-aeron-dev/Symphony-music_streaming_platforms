package com.phungquocthai.symphony.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Album;

import jakarta.transaction.Transactional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {

    @Query("SELECT a FROM Album a WHERE a.singer.singer_id = :singerId")
    List<Album> getBySingerId(@Param("singerId") Integer singerId);

    @Modifying
    @Transactional
    @Procedure(name = "delete_album")
    void delete_album(@Param("p_album_id") Integer albumId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO album_song (album_id, song_id) VALUES (:albumId, :songId)", nativeQuery = true)
    void addSongToAlbum(@Param("albumId") Integer albumId, @Param("songId") Integer songId);

    @Query(value = "SELECT album_id FROM album_song WHERE album_id = :albumId AND song_id = :songId", nativeQuery = true)
    Integer isSongInAlbum(@Param("albumId") Integer albumId, @Param("songId") Integer songId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM album_song WHERE album_id = :albumId AND song_id = :songId", nativeQuery = true)
    void removeSongFromAlbum(@Param("albumId") Integer albumId, @Param("songId") Integer songId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM album_song WHERE song_id = :songId", nativeQuery = true)
    void deleteAllBySongId(@Param("songId") Integer songId);
}

