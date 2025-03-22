package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.phungquocthai.symphony.entity.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM favorite WHERE song_id = :songId AND user_id = :userId", nativeQuery = true)
	Integer deleteBySongIdAndUserId(@Param("songId") Integer songId, @Param("userId") Integer userId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO favorite (song_id, user_id) VALUES (:songId, :userId)", nativeQuery = true)
	Integer insertFavorite(@Param("songId") Integer songId, @Param("userId") Integer userId);
	
	@Query(value = "SELECT user_id FROM favorite WHERE song_id =:songId AND user_id =:userId", nativeQuery = true)
	Integer findFavorited(@Param("songId") Integer songId, @Param("userId") Integer userId);
	
	@Query(value = "SELECT COUNT(*) FROM favorite WHERE song_id = :songId AND user_id = :userId limit 1", nativeQuery = true)
	Integer findByPrimaryKey(@Param("songId") Integer songId, @Param("userId") Integer userId);

}
