package com.system.blog.application.service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.system.blog.application.entity.Post;
import com.system.blog.application.entity.User;
import com.system.blog.application.exception.FailedToDeleteException;
import com.system.blog.application.exception.FailedToUpdateException;
import com.system.blog.application.exception.PostNotFoundException;
import com.system.blog.application.jpa.PostRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostService {

    private PostRepository postRepository;
    private UserService userService;

    public List<Post> getUserPosts(Long userId) {
        return postRepository.findByUserOrderByCreatedAtDesc(userService.findUserById(userId));
    }

    public Post createPostForUser(Long userId, String content) {
        User user = userService.findUserById(userId);

        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        post.setCreatedAt(new Date());
        
        user.addPost(post);
        return postRepository.save(post);
    }

    public Post updateExistingPost(Long userId, Long postId, String updatedContent) {
        Post existingPost = findPostById(postId);

        if (!existingPost.getUser().getId().equals(userId)) {
            throw new FailedToUpdateException("Failed to update post with id: " + postId);
        }

        existingPost.setContent(updatedContent);
        return postRepository.save(existingPost);
    }

    public List<Post> getFeedForUser(Long userId) { 
        User user = userService.findUserById(userId);

        return user.getPosts().stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    public Post findPostById(Long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if(post.isEmpty())
            throw new PostNotFoundException("Failed to find post with id: " + postId);
        return post.get();
    }

    public void removePostById(Long userId, Long postId) {
        Post post = findPostById(postId);
        if (!post.getUser().getId().equals(userId)) {
            throw new FailedToDeleteException("Failed to delete post with id: " + postId);
        }

        User user = post.getUser();
        user.removePost(post);
        postRepository.delete(post);
    }

}
