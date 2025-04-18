package com.phungquocthai.symphony.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.phungquocthai.symphony.entity.Singer;

import jakarta.transaction.Transactional;

@Repository
public interface SingerRepository extends JpaRepository<Singer, Integer> {
	@Query("SELECT s FROM Singer s WHERE s.stageName LIKE %:stageName%")
	List<Singer> getByStageName(@Param("stageName") String stageName);
	
	@Modifying
	@Transactional
	@Query(value =  "DELETE FROM present p WHERE p.singer_id = :singerId AND p.song_id = :songId"
			,nativeQuery = true)
	void deletePresent(@Param("singerId") Integer singerId, @Param("songId") Integer songId);
	
	@Modifying
	@Transactional
	@Query(value =  "INSERT INTO present (singer_id, song_id) VALUES (:singerId, :songId)"
			,nativeQuery = true)
	void addPresent(@Param("singerId") Integer singerId, @Param("songId") Integer songId);
	
	@Query(value = "SELECT singer_id FROM present WHERE singer_id = :singerId AND song_id = :songId", nativeQuery = true)
	Integer isPresented(@Param("singerId") Integer singerId, @Param("songId") Integer songId);
	
	@Query(value = "SELECT song_id FROM present WHERE song_id = :songId", nativeQuery = true)
	Integer havePresent(@Param("songId") Integer songId);
	
	@Modifying
	@Transactional
	@Query(value =  "INSERT INTO category_song (category_id, song_id) VALUES (:categoryId, :songId)"
			,nativeQuery = true)
	void addCategoryForSong(@Param("categoryId") Integer categoryId, @Param("songId") Integer songId);
	
	@Query(value = "SELECT song_id FROM category_song WHERE category_id = :categoryId AND song_id = :songId", nativeQuery = true)
	Integer isCategoryOfSong(@Param("categoryId") Integer categoryId, @Param("songId") Integer songId);
	
	@Query(value = "SELECT s.* FROM singer s " +
            "INNER JOIN present p ON s.singer_id = p.singer_id " +
            "WHERE p.song_id = :songId", 
    nativeQuery = true)
	List<Singer> findBySongId(@Param("songId") Integer songId);
	
	@Query(value = "SELECT s.* FROM singer s " +
            "INNER JOIN present p ON s.singer_id = p.singer_id " +
            "WHERE p.song_id IN (:ids)", 
    nativeQuery = true)
	List<Singer> findAllBySongId(@Param("ids") Iterable<Integer> ids);
	
	@Query(value = "SELECT * FROM singer WHERE user_id = :userId", nativeQuery = true)
	Optional<Singer> findByUserId(@Param("userId") Integer userId);
	
	@Query(value = "SELECT * FROM singer WHERE singer_id NOT IN (:ids)", 
    nativeQuery = true)
	List<Singer> findAllBySongIdNotIn(@Param("ids") Iterable<Integer> ids);
	
	List<Singer> findByStageNameContainingIgnoreCase(String keyword);

}
