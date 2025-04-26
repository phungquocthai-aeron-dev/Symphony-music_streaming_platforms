package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.phungquocthai.symphony.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
	 @Modifying
	 @Transactional
	 @Query(value = "DELETE FROM category_song WHERE song_id = :songId", nativeQuery = true)
	 void deleteAllBySongId(@Param("songId") Integer songId);
}
