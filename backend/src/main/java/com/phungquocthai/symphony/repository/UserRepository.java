package com.phungquocthai.symphony.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	boolean existsByPhone(String phone);
	Optional<User> findByPhone(String phone);
	List<User> findByRole(String role);
}
