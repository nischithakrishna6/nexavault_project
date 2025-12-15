// ============================================
// FILE 1: src/main/java/com/fintech/platform/service/AuthService.java
// ============================================
package com.fintech.platform.service;

import com.fintech.platform.dto.AuthResponse;
import com.fintech.platform.dto.LoginRequest;
import com.fintech.platform.dto.RegisterRequest;
import com.fintech.platform.model.User;
import com.fintech.platform.repository.UserRepository;
import com.fintech.platform.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fintech.platform.util.PasswordValidator;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Validate password strength
        PasswordValidator.ValidationResult validation = PasswordValidator.validate(
                request.getPassword(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName()
        );

        if (!validation.isValid()) {
            throw new RuntimeException(validation.getError());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setIsActive(true);

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateTokenFromEmail(savedUser.getEmail());
        String refreshToken = tokenProvider.generateRefreshToken(savedUser.getEmail());

        return new AuthResponse(
                token,
                refreshToken,
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFirstName(),
                savedUser.getLastName()
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthResponse(
                token,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName()
        );
    }

    public AuthResponse refreshToken(String refreshToken) {
        if (tokenProvider.validateToken(refreshToken)) {
            String email = tokenProvider.getEmailFromToken(refreshToken);
            String newToken = tokenProvider.generateTokenFromEmail(email);
            String newRefreshToken = tokenProvider.generateRefreshToken(email);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            return new AuthResponse(
                    newToken,
                    newRefreshToken,
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName()
            );
        }
        throw new RuntimeException("Invalid refresh token");
    }
}
