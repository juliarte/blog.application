package com.system.blog.application.controller;

import org.springframework.web.bind.annotation.RestController;

import com.system.blog.application.model.JwtResponse;
import com.system.blog.application.model.RegistrationRequest;

import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@AllArgsConstructor
public class JwtAuthenticationController {

	private JwtEncoder jwtEncoder;

	private AuthenticationManager authenticationManager;

	@PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticate(@RequestBody RegistrationRequest loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        // Generate JWT token
        String token = createToken(authentication);

        // Return JWT token in response
        return ResponseEntity.ok(new JwtResponse(token));
    }

	// @PostMapping("/authenticate") 
	// public JwtResponse authenticate(Authentication authentication) {
	// 	return new JwtResponse(createToken(authentication));
	// }

	private String createToken(Authentication authentication) {
		JwtClaimsSet jwtClaims = JwtClaimsSet.builder()
								.issuer("self")
								.issuedAt(Instant.now())
								.expiresAt(Instant.now().plusSeconds(60 * 30))
								.subject(authentication.getName())
								.claim("scope", createScope(authentication))
								.build();
		
		return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaims))
						.getTokenValue();
	}

	private String createScope(Authentication authentication) {
		return authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(" "));			
	}
    
}

//record JwtRespose(String token) {}
