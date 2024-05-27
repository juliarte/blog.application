package com.system.blog.application.it.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.system.blog.application.entity.Comment;
import com.system.blog.application.entity.Post;
import com.system.blog.application.entity.User;
import com.system.blog.application.exception.PostNotFoundException;
import com.system.blog.application.exception.UserNotFoundException;
import com.system.blog.application.jpa.CommentRepository;
import com.system.blog.application.jpa.PostRepository;
import com.system.blog.application.jpa.UserRepository;
import com.system.blog.application.service.CommentService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CommentServiceIT {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user = userRepository.save(user);

        post = new Post();
        post.setUser(user);
        post.setContent("testContent");
        post.setCreatedAt(new Date());
        post = postRepository.save(post);
    }

    @AfterEach
    void cleanUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getCommentsByPost_ShouldReturnPostComments() {
        createComment("testComment1");
        createComment("testComment2");

        List<Comment> actualComments = commentService.getCommentsByPost(post);

        assertEquals(2, actualComments.size());
        assertEquals("testComment1", actualComments.get(0).getContent());
        assertEquals("testComment2", actualComments.get(1).getContent());
    }

    @Test
    void addCommentToPost_ShouldAddCommentToPost() {
        String commentContent = "newComment";
        Comment newComment = commentService.addCommentToPost(user.getId(), post.getId(), commentContent);

        assertNotNull(newComment.getId());
        assertEquals(commentContent, newComment.getContent());
        assertEquals(user.getId(), newComment.getUser().getId());
        assertEquals(post.getId(), newComment.getPost().getId());

        List<Comment> comments = commentService.getCommentsByPost(post);
        assertEquals(1, comments.size());
        assertEquals(commentContent, comments.get(0).getContent());
    }

    @Test
    void addCommentToPost_ShouldThrowException_WhenUserNotFound() {
        Long invalidUserId = -1L;
        String commentContent = "commentContent";

        Exception actualException = assertThrows(UserNotFoundException.class,
                () -> commentService.addCommentToPost(invalidUserId, post.getId(), commentContent));

        String expectedMessage = "Failed to find user with id: " + invalidUserId;
        String actualMessage = actualException.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void addCommentToPost_ShouldThrowException_WhenPostNotFound() {
        Long invalidPostId = -1L;
        String commentContent = "commentContent";

        Exception actualException = assertThrows(PostNotFoundException.class,
                () -> commentService.addCommentToPost(user.getId(), invalidPostId, commentContent));

        String expectedMessage = "Failed to find post with id: " + invalidPostId;
        String actualMessage = actualException.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    private void createComment(String content) {
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        comment.setCreatedAt(new Date());
        commentRepository.save(comment);
    }
}
