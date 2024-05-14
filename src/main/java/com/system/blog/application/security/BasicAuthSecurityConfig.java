package com.system.blog.application.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

//alternative auth method
//@Configuration
public class BasicAuthSecurityConfig {

    @Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests(
						auth -> {
							auth
                            .requestMatchers("/api/users").hasRole("USER")
							.anyRequest().authenticated();
						});
		
		http.sessionManagement(
						session -> 
							session.sessionCreationPolicy(
									SessionCreationPolicy.STATELESS)
						);
		
		http.httpBasic(withDefaults());
		
		http.csrf(csrf -> csrf.disable());
		
		http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));
		
		return http.build();
	}

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user1 = User.withUsername("user1")
            .password(passwordEncoder.encode("password1"))
            .roles("USER")
            .build();

        UserDetails user2 = User.withUsername("user2")
            .password(passwordEncoder.encode("password2"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(user1, user2);
    }

    @Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    
}
