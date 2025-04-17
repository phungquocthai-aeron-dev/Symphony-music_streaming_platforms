package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

}
