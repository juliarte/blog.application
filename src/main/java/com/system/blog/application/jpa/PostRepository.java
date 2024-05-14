package com.system.blog.application.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.blog.application.entity.Post;
import com.system.blog.application.entity.User;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserInOrderByCreatedAtDesc(List<User> users);

    List<Post> findByUserOrderByCreatedAtDesc(User user);
    
}
