package com.system.blog.application.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.system.blog.application.entity.Comment;
import com.system.blog.application.entity.Like;
import com.system.blog.application.entity.Post;
import com.system.blog.application.service.CommentService;
import com.system.blog.application.service.LikeService;
import com.system.blog.application.service.PostService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("api/users/{userId}")
public class PostController {

    private PostService postService;
    private CommentService commentService;
    private LikeService likeService;

    @GetMapping("/posts")
    public ResponseEntity<List<Post>> retrieveAllPostsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getUserPosts(userId));
    }

    @PostMapping("/posts")
    public ResponseEntity<Post> createPost(@PathVariable Long userId, @Valid @RequestBody String content) {
        Post savedPost = postService.createPostForUser(userId, content);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{postId}")
                .buildAndExpand(savedPost.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Long userId, @PathVariable Long postId,
            @Valid @RequestBody String updatedContent) {
        return ResponseEntity.ok(postService.updateExistingPost(userId, postId, updatedContent));
    }

    @GetMapping("/feed")
    public ResponseEntity<List<Post>> getUserFeed(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getFeedForUser(userId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable Long userId, @PathVariable Long postId,
            @RequestParam String commentContent) {
        Comment newComment = commentService.addCommentToPost(userId, postId, commentContent);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{commentId}")
                .buildAndExpand(newComment.getId())
                .toUri();
        return ResponseEntity.created(location).body(newComment);
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> toggleLike(@PathVariable Long userId, @PathVariable Long postId) {
        Like like = likeService.toggleLike(userId, postId);

        if (like == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(like);
        }
    }

}
