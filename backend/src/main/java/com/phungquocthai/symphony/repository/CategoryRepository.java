package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
