package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Album;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {

}
