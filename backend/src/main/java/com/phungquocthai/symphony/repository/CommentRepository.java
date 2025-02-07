package com.phungquocthai.symphony.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phungquocthai.symphony.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

}
