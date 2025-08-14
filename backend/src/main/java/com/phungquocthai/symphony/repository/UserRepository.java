package com.phungquocthai.symphony.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.phungquocthai.symphony.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	boolean existsByPhone(String phone);
	Optional<User> findByPhone(String phone);
	List<User> findByRole(String role);
	
	List<User> findByFullNameContainingIgnoreCaseOrPhoneContaining(String fullName, String phone);	
	
	@Query(value = "SELECT * FROM user NATURAL JOIN singer WHERE singer_id = :singerId", nativeQuery = true)
	Optional<User> findBySingerId(@Param("singerId") Integer singerId);
	
    @Query(value = "SELECT thong_ke_doanh_thu_theo_thang(:thang, :nam)", nativeQuery = true)
    Float thongKeTheoThang(@Param("thang") int thang, @Param("nam") int nam);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET role = 'SINGER' WHERE user_id = :userId", nativeQuery = true)
    void updateUserRoleToSinger(@Param("userId") Integer userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user SET role = 'USER' WHERE user_id = :userId", nativeQuery = true)
    void updateUserRoleToUser(@Param("userId") Integer userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.active = :status WHERE u.userId = :userId")
    int updateIsActive(@Param("userId") Integer userId, @Param("status") boolean status);
}
