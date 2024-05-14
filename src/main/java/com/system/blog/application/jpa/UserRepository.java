package com.system.blog.application.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.blog.application.entity.User;



public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
