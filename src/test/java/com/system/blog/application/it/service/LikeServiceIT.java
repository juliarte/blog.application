package com.system.blog.application.it.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import com.system.blog.application.entity.Like;
import com.system.blog.application.entity.Post;
import com.system.blog.application.entity.User;
import com.system.blog.application.exception.PostNotFoundException;
import com.system.blog.application.exception.UserNotFoundException;
import com.system.blog.application.jpa.LikeRepository;
import com.system.blog.application.jpa.PostRepository;
import com.system.blog.application.jpa.UserRepository;
import com.system.blog.application.service.LikeService;
import com.system.blog.application.service.PostService;
import com.system.blog.application.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LikeServiceIT {

    @Autowired
    public LikeService likeService;

    @Autowired
    public UserService userService;

    @Autowired
    public PostService postService;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

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
        likeRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getLikesByPost_ShouldReturnLikes() {
        newLike();
        newLike();

        List<Like> likes = likeService.getLikesByPost(post);
        assertEquals(2, likes.size());
        assertEquals(user.getId(), likes.get(0).getUser().getId());
        assertEquals(user.getId(), likes.get(1).getUser().getId());
    }

    @Test
    void toggleLike_ShouldAddLike() {
        Like like = likeService.toggleLike(user.getId(), post.getId());
        
        assertNotNull(like);
        assertEquals(user.getId(), like.getUser().getId());
        assertEquals(post.getId(), like.getPost().getId());
        
        List<Like> likes = likeService.getLikesByPost(post);
        assertEquals(1, likes.size());
    }

    @Test
    void toggleLike_ShouldRemoveLike() {
        Like like = newLike();

        like = likeService.toggleLike(user.getId(), post.getId());
        
        assertNull(like);
        
        List<Like> actualLikes = likeService.getLikesByPost(post);
        assertEquals(0, actualLikes.size());
    }

    @Test
    void toggleLike_ShouldThrowException_WhenUserNotFound() {
        Long invalidUserId = -1L;
        Long postId = post.getId();

        Exception actualException = assertThrows(UserNotFoundException.class, () ->
            likeService.toggleLike(invalidUserId, postId)
        );

        String expectedMessage = "Failed to find user with id: " + invalidUserId;
        String actualMessage = actualException.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void toggleLike_ShouldThrowException_WhenPostNotFound() {
        Long userId = user.getId();
        Long invalidPostId = -1L;

        Exception actualException = assertThrows(PostNotFoundException.class, () ->
            likeService.toggleLike(userId, invalidPostId)
        );

        String expectedMessage = "Failed to find post with id: " + invalidPostId;
        String actualMessage = actualException.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
    
    private Like newLike() {
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);
        return like;
    }
    
}
