package com.system.blog.application.it.controller;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.system.blog.application.entity.Post;
import com.system.blog.application.entity.User;
import com.system.blog.application.it.util.JwtTestUtil;
import com.system.blog.application.jpa.PostRepository;
import com.system.blog.application.jpa.UserRepository;
import com.system.blog.application.service.CommentService;
import com.system.blog.application.service.LikeService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class PostControllerIT {

    private static final String BASE_URL = "/api/users/{userId}";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private JwtEncoder jwtEncoder;

    private User user;
    private Post post;
    private String jwtToken;

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

        JwtTestUtil jwtUtil = new JwtTestUtil(jwtEncoder);
        jwtToken = jwtUtil.generateToken(user.getUsername());
    }

    @AfterEach
    void cleanUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void retrieveAllPostsForUser_ShouldReturnAllPosts() throws Exception {
        mockMvc.perform(get(BASE_URL + "/posts", user.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content", is(post.getContent())));
    }

    @Test
    void createPost_ShouldCreatePost() throws Exception {
        mockMvc.perform(post(BASE_URL + "/posts", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("testContent")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated());
    }

    @Test
    void updatePost_ShouldUpdate() {
        
    }

}
