package com.phungquocthai.symphony.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.phungquocthai.symphony.entity.Song;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
	@Query(value =  "select s.* from song s natural join favorite f where f.user_id =:userId", nativeQuery = true)
	List<Song> getFavoriteSongsOfUser(@Param("userId") Integer userId);
	
//	@Query(value =  "SELECT s.*, MAX(l.listen_at) as lastListenAt "
//			+ "FROM song s JOIN listen l ON s.song_id = l.song_id "
//			+ "WHERE l.user_id = :userId "
//			+ "GROUP BY s.song_id "
//			+ "ORDER BY lastListenAt DESC "
//			+ "LIMIT :limit", nativeQuery = true)
//	List<Song> getRecentlyListenSongs(@Param("userId") Integer userId,
//			@Param("limit") Integer limit);
//	
	@Modifying
    @Transactional
	@Query(value =  "INSERT INTO present (singer_id, song_id) VALUES (:singerId, :songId)", nativeQuery = true)
    int addSongToSinger(@Param("singerId") Integer singerId, @Param("songId") Integer songId);
	
	@Modifying
    @Transactional
	@Query(value =  "INSERT INTO category_song (category_id, song_id) VALUES (:categoryId, :songId)", nativeQuery = true)
    int addSongToCategory(@Param("categoryId") Integer categoryId, @Param("songId") Integer songId);

	
	@Query(value = "SELECT * FROM song WHERE release_date >= date_sub(CURDATE(), INTERVAL 1 MONTH) ORDER BY release_date DESC LIMIT :limit", 
	           nativeQuery = true)
	List<Song> findSongsFromLastYear(@Param("limit") Integer limit);
	
	@Query(value = "SELECT * FROM song ORDER BY release_date DESC, song_id DESC LIMIT 1", nativeQuery = true)	
	Optional<Song> getNewSong();

	@Query(value = "SELECT DISTINCT s.* " +
            "FROM song s " +
            "NATURAL JOIN category_song p " +
            "NATURAL JOIN category c " +
            "WHERE s.song_name LIKE :key OR c.category_name LIKE :key " +
            "COLLATE utf8mb4_unicode_ci", nativeQuery = true)
	List<Song> searchSong(@Param("key") String key);
	
	@Query(value = "SELECT s.* " +
			"FROM song s " +
            "NATURAL JOIN present p " +
            "NATURAL JOIN category c " +
			"WHERE c.category_id = :categoryId", nativeQuery = true)
	List<Song> getSongsByCategory(@Param("categoryId") Integer categoryId);
	
	@Query(value = "SELECT DISTINCT s.* " +
			"FROM song s " +
            "NATURAL JOIN present p " +
			"NATURAL JOIN category_song cs " +
            "NATURAL JOIN category c " +
			"WHERE c.category_name = :categoryName " +
			"LIMIT :limit", nativeQuery = true)
	List<Song> getSongsByCategory(@Param("categoryName") String categoryName,
			@Param("limit") Integer limit);
	
	@Query(value = "SELECT s.* FROM song s " +
            "INNER JOIN present p ON s.song_id = p.song_id " +
            "WHERE p.singer_id = :singerId", 
    nativeQuery = true)
	List<Song> findBySingerId(@Param("singerId") Integer singerId);
	
	@Query(value = "SELECT s.* FROM song s " +
            "INNER JOIN present p ON s.song_id = p.song_id " +
            "WHERE p.singer_id IN (:ids)", 
    nativeQuery = true)
	List<Song> findAllBySingerId(@Param("ids") Iterable<Integer> ids);
	
	@Query(value = "SELECT s.* FROM song s " +
            "INNER JOIN category_song cs ON s.song_id = cs.song_id " +
            "WHERE cs.category_id IN (:ids)", 
    nativeQuery = true)
	List<Song> findAllByCategoryId(@Param("ids") List<Integer> ids);
	
	@Query(value = "SELECT s.* FROM song s " +
            "INNER JOIN category_song cs ON s.song_id = cs.song_id " +
            "WHERE cs.category_id IN (:ids) AND s.song_id NOT IN (:notInSongIds)", 
    nativeQuery = true)
	List<Song> findAllByCategoryIdNotIn(
			@Param("ids") Iterable<Integer> ids,
			@Param("notInSongIds") Iterable<Integer> notInSongIds);
	
	@Query(value = "SELECT s.* FROM song s "
			+ "WHERE listens >= 100000000 "
			+ "ORDER BY listens DESC "
			+ "LIMIT :limit", nativeQuery = true)
	List<Song> findHotHit(@Param("limit") Integer limit);
	
	@Query(value = "SELECT p.singer_id FROM present p WHERE song_id = :songId LIMIT 1", nativeQuery = true)
	Optional<Integer> findFirstBySongId(@Param("songId") Integer songId);
	
//	@Query(value = """
//		    SELECT s.*, COUNT(l.user_id) AS total_listens_per_hour
//		    FROM listen l INNER JOIN song s ON l.song_id = s.song_id
//		    WHERE l.listen_at < DATE_FORMAT(NOW(), "%Y-%m-%d %H:00:00")  
//		          AND l.listen_at >= DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 HOUR), "%Y-%m-%d %H:00:00")
//		    GROUP BY s.song_id
//		    ORDER BY total_listens_per_hour DESC, s.release_date DESC, s.duration DESC 
//		    LIMIT :limit
//		    """, nativeQuery = true)
//		List<Object[]> getTopSongsLastHour(@Param("limit") Integer limit);
		
		@Query(value = """
			    SELECT 
			        s.song_id,
			        s.author,
			        s.duration,
			        s.is_vip,
			        s.lrc,
			        s.lyric,
			        s.path,
			        s.release_date,
			        s.song_name,
			        s.song_img,
			        s.listens,
			        COUNT(l.user_id) AS total_listens_per_hour
			    FROM listen l 
			    INNER JOIN song s ON l.song_id = s.song_id
			    WHERE l.listen_at < DATE_FORMAT(NOW(), "%Y-%m-%d %H:00:00")  
			          AND l.listen_at >= DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 HOUR), "%Y-%m-%d %H:00:00")
			    GROUP BY 
			        s.song_id
			    ORDER BY total_listens_per_hour DESC, s.release_date DESC, s.duration DESC 
			    LIMIT :limit
			    """, nativeQuery = true)
			List<Object[]> getTopSongsLastHour(@Param("limit") Integer limit);

	@Query(value = """
		    SELECT song_id, DATE(listen_at) AS listen_date, 
		           EXTRACT(HOUR FROM listen_at) AS hour, 
		           COUNT(user_id) AS total_listens_per_hour
		    FROM listen
		    WHERE song_id = :songId AND listen_at < DATE_FORMAT(NOW(), '%Y-%m-%d %H:00:00')
		    GROUP BY song_id, hour, listen_date
		    ORDER BY song_id, listen_date DESC, hour DESC
			    """, nativeQuery = true)
			List<Object[]> getHourlyListeningStats(@Param("songId") Integer songId);
			
	@Query(value = """
	        SELECT s.* 
	        FROM listen l 
	        NATURAL JOIN song s
	        WHERE l.user_id = :userId
	        GROUP BY s.song_id
			ORDER BY MAX(l.listen_at) DESC
			LIMIT :limit
	        """, nativeQuery = true)
	    List<Song> findRecentlyListenedSongs(@Param("userId") Integer userId, @Param("limit") Integer limit);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO listen (user_id, listen_at, song_id) VALUES (:userId, NOW(), :songId)", nativeQuery = true)
	int addListened(@Param("userId") Integer userId, @Param("songId") Integer songId);
	
}
