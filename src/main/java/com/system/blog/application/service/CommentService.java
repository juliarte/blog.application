package com.system.blog.application.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.system.blog.application.entity.Comment;
import com.system.blog.application.entity.Post;
import com.system.blog.application.jpa.CommentRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {

    private CommentRepository commentRepository;
    private PostService postService;
    private UserService userService;

    public List<Comment> getCommentsByPost(Post post) {
        return commentRepository.findByPost(post);
    }

    public Comment addCommentToPost(Long userId, Long postId, String content) {
        Comment comment = new Comment();
        comment.setUser(userService.findUserById(userId));
        comment.setPost(postService.findPostById(postId));
        comment.setContent(content);
        comment.setCreatedAt(new Date());
        return commentRepository.save(comment);
    }
    
}
