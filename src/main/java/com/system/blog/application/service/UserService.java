package com.system.blog.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.system.blog.application.entity.User;
import com.system.blog.application.exception.UserNotFoundException;
import com.system.blog.application.jpa.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public User registerUser(String username, String encodedPassword) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        
        userRepository.save(user);
        return user;
    }

    public User findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty())
            throw new UserNotFoundException("Failed to find user with id: " + userId);

        return user.get();
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void updateUser(User updatedUser) {
        User existingUser = findUserById(updatedUser.getId());
        BeanUtils.copyProperties(updatedUser, existingUser, "id");
        userRepository.save(existingUser);

    }

    public void followUser(Long userId, Long followerUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        User followerUser = userRepository.findById(followerUserId)
                .orElseThrow(() -> new EntityNotFoundException("Follower user not found with id: " + followerUserId));

        user.getFollowings().add(followerUser);
        userRepository.save(user);
    }

    public void unfollowUser(Long userId, Long followerUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        User followerUser = userRepository.findById(followerUserId)
                .orElseThrow(() -> new EntityNotFoundException("Target user not found with id: " + followerUserId));

        user.getFollowings().remove(followerUser);
        userRepository.save(user);
    }

    public List<User> getUserFollowings(Long userId) {
        User user = findUserById(userId);
        return user.getFollowings();
    }

}
