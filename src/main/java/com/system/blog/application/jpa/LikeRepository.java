package com.system.blog.application.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.blog.application.entity.Like;
import com.system.blog.application.entity.Post;
import com.system.blog.application.entity.User;

public interface LikeRepository extends JpaRepository<Like, Long> {
    
    List<Like> findByPost(Post post);

    boolean existsByUserAndPost(User user, Post post);

    void deleteByUserAndPost(User user, Post post);
    
}
