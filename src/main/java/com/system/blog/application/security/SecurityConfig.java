package com.system.blog.application.security;

import static org.springframework.security.config.Customizer.withDefaults;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class SecurityConfig {

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
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests(
						auth -> {
							auth
							.requestMatchers(HttpMethod.POST, "/login").permitAll()
							.requestMatchers("/register").permitAll()
							.anyRequest().authenticated();
						});
		
		http.sessionManagement(
						session -> 
							session.sessionCreationPolicy(
									SessionCreationPolicy.STATELESS)
						);
		
		http.httpBasic(withDefaults());

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"));
		
		http.csrf(csrf -> csrf.disable());

        http.headers(headers -> headers.frameOptions(FrameOptionsConfig::disable));

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
		
		return http.build();
	}

    @Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
	public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
		return new NimbusJwtEncoder(jwkSource);
	}

    @Bean
	public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
		return NimbusJwtDecoder
				.withPublicKey(rsaKey.toRSAPublicKey())
				.build();
		
	}

    @Bean
    public KeyPair keyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Failed to generate key pair", ex);
        }
    }
	
	@Bean
	public RSAKey rsaKey(KeyPair keyPair) {
		return new RSAKey.Builder((RSAPublicKey)keyPair.getPublic())
				.privateKey(keyPair.getPrivate())
                .keyUse(KeyUse.SIGNATURE)
				.keyID(UUID.randomUUID().toString())
				.build();
	}

	@Bean
	public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
		JWKSet jwkSet = new JWKSet(rsaKey);
		return (jwkSelector, securityContext) ->  jwkSelector.select(jwkSet);
		
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
     	return authenticationConfiguration.getAuthenticationManager();
	}
	
    
}
