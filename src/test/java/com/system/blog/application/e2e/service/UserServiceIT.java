package com.system.blog.application.e2e.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.system.blog.application.entity.User;
import com.system.blog.application.exception.UserNotFoundException;
import com.system.blog.application.jpa.UserRepository;
import com.system.blog.application.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIT {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setPassword("encodedPassword");
        testUser = userRepository.save(testUser);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void findUserById_ShouldReturnUser() {
        User foundUser = userService.findUserById(testUser.getId());
        System.out.println(foundUser);

        assertNotNull(foundUser);
        assertEquals(testUser.getId(), foundUser.getId());
        assertEquals(testUser.getUsername(), foundUser.getUsername());
        assertEquals(testUser.getPassword(), foundUser.getPassword());
    }

    @Test
    void findUserById_ShouldThrowException_WhenUserNotFound() {
        Long invalidUserId = -1L;

        Exception actualException = assertThrows(UserNotFoundException.class, () -> {
            userService.findUserById(invalidUserId);
        });

        String expectedMessage = "Failed to find user with id: " + invalidUserId;
        assertTrue(actualException.getMessage().contains(expectedMessage));
    }

    @Test
    void findUserByUserName_ShouldFindUser() {
        Optional<User> actualUser = userService.findUserByUsername(testUser.getUsername());

        assertTrue(actualUser.isPresent());
        assertEquals(testUser.getId(), actualUser.get().getId());
        assertEquals(testUser.getUsername(), actualUser.get().getUsername());
        assertEquals(testUser.getPassword(), actualUser.get().getPassword());
    }

    @Test
    void retrieveAllUsers_ShouldReturnAllUsers() {
        setUpNewUser();
        List<User> actualUsers = userService.getAllUsers();

        assertEquals(2, actualUsers.size());
    }

    @Test
    void updateUser_ShouldUpdateUser() {
        testUser.setUsername("updUsername");
        testUser.setPassword("updPassword");

        userService.updateUser(testUser);

        User foundUser = userService.findUserById(testUser.getId());

        assertNotNull(foundUser);
        assertEquals("updUsername", foundUser.getUsername());
        assertEquals("updPassword", foundUser.getPassword());
    }

    @Test
    void followUser_ShouldAddNewFollowing() {
        User newUser = setUpNewUser();

        userService.followUser(testUser.getId(), newUser.getId());

        User foundUser = userService.findUserById(testUser.getId());
        List<User> foundUserFollowings = foundUser.getFollowings();

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
        assertEquals(1, foundUserFollowings.size());
        assertEquals("newUser", foundUserFollowings.get(0).getUsername());
    }

    @Test
    void unfollowUser_ShouldRemoveFollowingUser() {
        User newUser = setUpNewUser();
        userService.followUser(testUser.getId(), newUser.getId());

        userService.unfollowUser(testUser.getId(), newUser.getId());

        User foundUser = userService.findUserById(testUser.getId());

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
        assertEquals(0, foundUser.getFollowings().size());

    }

    private User setUpNewUser() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("newUserPassword");
        newUser = userRepository.save(newUser);

        return newUser;
    }
    
}
