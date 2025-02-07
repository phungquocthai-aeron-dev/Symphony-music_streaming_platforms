package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.phungquocthai.symphony.entity.InvalidatedToken;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {

}
