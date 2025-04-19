package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Listen;

@Repository
public interface ListenRepository extends JpaRepository<Listen, Integer> {

    @Query(value = "SELECT thong_ke_luot_nghe_theo_thang(:thang, :nam)", nativeQuery = true)
    Integer thongKeTheoThang(@Param("thang") int thang, @Param("nam") int nam);
}
