package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Listen;

import jakarta.transaction.Transactional;

@Repository
public interface ListenRepository extends JpaRepository<Listen, Integer> {

    @Query(value = "SELECT thong_ke_luot_nghe_theo_thang(:thang, :nam)", nativeQuery = true)
    Integer thongKeTheoThang(@Param("thang") int thang, @Param("nam") int nam);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM listen WHERE user_id = :userId", nativeQuery = true)
    void deleteAllByUserId(@Param("userId") Integer userId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM listen WHERE song_id = :songId", nativeQuery = true)
    void deleteAllBySongId(@Param("songId") Integer songId);
}
