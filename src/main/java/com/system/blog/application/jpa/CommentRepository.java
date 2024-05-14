package com.system.blog.application.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.blog.application.entity.Comment;
import com.system.blog.application.entity.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    
}
