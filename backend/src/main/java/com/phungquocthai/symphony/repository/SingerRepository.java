package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Singer;

@Repository
public interface SingerRepository extends JpaRepository<Singer, Integer> {

}
