package com.system.blog.application.e2e.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.system.blog.application.entity.Post;
import com.system.blog.application.entity.User;
import com.system.blog.application.exception.FailedToDeleteException;
import com.system.blog.application.exception.FailedToUpdateException;
import com.system.blog.application.exception.PostNotFoundException;
import com.system.blog.application.jpa.PostRepository;
import com.system.blog.application.jpa.UserRepository;
import com.system.blog.application.service.PostService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PostServiceIT {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Post testPost;
    private final String CONTENT = "newContent";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("encodedPassword");
        testUser = userRepository.save(testUser);

        testPost = new Post();
        testPost.setUser(testUser);
        testPost.setContent("testContent");
        testPost.setCreatedAt(new Date());
        testPost = postRepository.save(testPost);
    }

    @AfterEach
    void cleanUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getUserPosts_ShouldReturnPosts() {
        List<Post> posts = postService.getUserPosts(testUser.getId());
        
        assertEquals(1, posts.size());
        assertEquals("testContent", posts.get(0).getContent());
    }

    @Test
    void createPostForUser_ShouldCreateNewPost() {
        Post newPost = createNewPostForUser();
        
        assertNotNull(newPost.getId());
        assertEquals(CONTENT, newPost.getContent());
        assertEquals(testUser.getId(), newPost.getUser().getId());
    }

    @Test
    void updateExistingPost_ShouldUpdateExistingPost() {
        String updatedContent = "updatedContent";
        Post updatedPost = postService.updateExistingPost(testUser.getId(), testPost.getId(), updatedContent);
        
        assertEquals(updatedContent, updatedPost.getContent());
    }

    @Test
    void updateExistingPost_ShouldThrowException_IfUserIdDoesNotMatch() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("encodedPassword");
        final User savedOtherUser = userRepository.save(newUser);

        String updatedContent = "updatedContent";

        Exception actualException = assertThrows(FailedToUpdateException.class, () -> {
            postService.updateExistingPost(savedOtherUser.getId(), testPost.getId(), updatedContent);
        });

        String expectedMessage = "Failed to update post with id: " + testPost.getId();
        String actualMessage = actualException.getMessage();
        
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getFeedForUser_ShouldReturnFeedofPosts() {
        createNewPostForUser();
        List<Post> actualFeed = postService.getFeedForUser(testUser.getId());
        
        assertEquals(1, actualFeed.size());
        assertEquals("newContent", actualFeed.get(0).getContent());
    }

    @Test
    void findPostById_ShouldReturnTheCorrectPost() {
        Post foundPost = postService.findPostById(testPost.getId());
        
        assertEquals(testPost.getId(), foundPost.getId());
    }

    @Test
    void findPostById_ShouldThrowException_WhenPostNotFound() {
        Long invalidPostId = -1L;

        Exception actualException = assertThrows(PostNotFoundException.class, () -> {
            postService.findPostById(invalidPostId);
        });

        String expectedMessage = "Failed to find post with id: " + invalidPostId;
        String actualMessage = actualException.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void removePost_ShouldRemovePost() {
        postService.removePostById(testUser.getId(), testPost.getId());

        assertFalse(postRepository.findById(testPost.getId()).isPresent());
    }

    @Test
    void removePost_ShouldThrowException_WhenUserIdDoesNotMatch() {
        Exception actualException = assertThrows(FailedToDeleteException.class, () -> {
            postService.removePostById(111111L, testPost.getId());
        });

        String expectedMessage = "Failed to delete post with id: " + testPost.getId();
        String actualMessage = actualException.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    private Post createNewPostForUser() {
        return postService.createPostForUser(testUser.getId(), CONTENT);
    }
}
