package com.system.blog.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.system.blog.application.entity.Like;
import com.system.blog.application.entity.Post;
import com.system.blog.application.entity.User;
import com.system.blog.application.jpa.LikeRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LikeService {

    private LikeRepository likeRepository;
    private UserService userService;
    private PostService postService;

    public List<Like> getLikesByPost(Post post) {
        return likeRepository.findByPost(post);
    }

    public Like toggleLike(Long userId, Long postId) {
        User user = userService.findUserById(userId);
        Post post = postService.findPostById(postId);

        if (likeRepository.existsByUserAndPost(user, post)) {
            likeRepository.deleteByUserAndPost(user, post);
            return null;
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            return likeRepository.save(like);
        }
    }
    
}
