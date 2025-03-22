package com.phungquocthai.symphony.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.phungquocthai.symphony.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	boolean existsByPhone(String phone);
	Optional<User> findByPhone(String phone);
	List<User> findByRole(String role);
	
	@Query(value = "SELECT * FROM user NATURAL JOIN singer WHERE singer_id = :singerId", nativeQuery = true)
	Optional<User> findBySingerId(@Param("singerId") Integer singerId);
}
