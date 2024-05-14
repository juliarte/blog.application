package com.system.blog.application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.system.blog.application.entity.User;
import com.system.blog.application.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> retrieveAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.findUserById(userId));
    }

    @PostMapping
    public ResponseEntity<Void> updateUser(@RequestBody User updatedUser) {
        userService.updateUser(updatedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<List<User>> getUserFollowings(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserFollowings(userId));
    }

    @PostMapping("/{userId}/followings/{followingUserId}")
    public ResponseEntity<Void> followUser(@PathVariable Long userId, @PathVariable Long followingUserId) {
        userService.followUser(userId, followingUserId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/followings/{followingUserId}")
    public ResponseEntity<Void> unfollowUser(@PathVariable Long userId, @PathVariable Long followingUserId) {
        userService.unfollowUser(userId, followingUserId);
        return ResponseEntity.ok().build();
    }

    // Implement other user-related endpoints like fetching user profile, updating profile, etc.
    
}
