package com.system.blog.application.it.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.system.blog.application.entity.User;
import com.system.blog.application.it.util.JwtTestUtil;
import com.system.blog.application.jpa.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIT {

    private static final String BASE_URL = "/api/users";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtEncoder jwtEncoder;

    private User user;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user = userRepository.save(user);

        JwtTestUtil jwtUtil = new JwtTestUtil(jwtEncoder);
        jwtToken = jwtUtil.generateToken(user.getUsername());
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void retrieveAllUsers_ShouldReturnAllUsers() throws Exception {
        mockMvc.perform(get(BASE_URL)
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(user.getUsername())));
    }

    @Test
    void getUserById_ShouldReturnCertainUser() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{userId}", user.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())));
    }

    @Test
    void updateUser_ShouldUpdateUser() throws Exception {
        user.setUsername("updatedUser");

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": " + user.getId() + ", \"username\": \"updatedUser\", \"password\": \"encodedPassword\"}")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals("updatedUser", updatedUser.getUsername());
    }

    @Test
    void getUserFollowings_ShouldReturnUserFollowings() throws Exception {
        User followingUser = new User();
        followingUser.setUsername("followingUser");
        followingUser.setPassword("encodedPassword");
        followingUser = userRepository.save(followingUser);

        mockMvc.perform(post(BASE_URL + "/{userId}/followings/{followingUserId}", user.getId(), followingUser.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(get(BASE_URL + "/{userId}/followings", user.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is(followingUser.getUsername())));
    }

    @Test
    void followUser_ShouldFollowUser() throws Exception {
        User followingUser = new User();
        followingUser.setUsername("followingUser");
        followingUser.setPassword("encodedPassword");
        followingUser = userRepository.save(followingUser);

        mockMvc.perform(post(BASE_URL + "/{userId}/followings/{followingUserId}", user.getId(), followingUser.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(1, updatedUser.getFollowings().size());
        assertEquals("followingUser", updatedUser.getFollowings().get(0).getUsername());
    }

    @Test
    void unfollowUser_ShouldUnfollowUser() throws Exception {
        User followingUser = new User();
        followingUser.setUsername("followingUser");
        followingUser.setPassword("encodedPassword");
        followingUser = userRepository.save(followingUser);

        mockMvc.perform(post(BASE_URL + "/{userId}/followings/{followingUserId}", user.getId(), followingUser.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete(BASE_URL + "/{userId}/followings/{followingUserId}", user.getId(), followingUser.getId())
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(updatedUser.getFollowings().isEmpty());
    }
    
}
