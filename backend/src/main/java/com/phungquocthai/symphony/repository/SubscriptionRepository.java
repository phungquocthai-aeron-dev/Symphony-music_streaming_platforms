package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Subscription;

import jakarta.transaction.Transactional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
	
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM subscripttion WHERE user_id = :userId", nativeQuery = true)
	void deleteSubscriptionsByUserId(@Param("userId") Integer userId);


}
