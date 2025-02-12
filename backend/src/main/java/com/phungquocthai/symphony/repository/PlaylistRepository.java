package com.phungquocthai.symphony.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Playlist;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
	
	@Query("SELECT p FROM Playlist p WHERE p.user.userId = :userId")
	List<Playlist> getByUserId(@Param("userId") Integer userId);
}
