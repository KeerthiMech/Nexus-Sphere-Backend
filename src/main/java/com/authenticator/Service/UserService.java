package com.authenticator.Service;

import com.authenticator.Model.PasswordResetToken;
import com.authenticator.Model.Users;
import com.authenticator.dto.LoginRequest;
import com.authenticator.dto.LoginResponse;
import com.authenticator.dto.SignupRequest;
import com.authenticator.repository.PasswordResetTokenRepository;
import com.authenticator.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.authenticator.config.JwtUtil;


import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    // make repository final so Lombok @RequiredArgsConstructor will inject it
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();




    public String signup(SignupRequest signupRequest) {
        try {

            if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
                return "Mail already exists";
            }
            if (userRepository.findByUsername(signupRequest.getUsername()).isPresent()) {
                return "User already exists";
            }

            Users user = new Users();
            user.setUserId(UUID.randomUUID().toString());
            user.setEmail(signupRequest.getEmail());
            user.setUsername(signupRequest.getUsername());
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);

            return "User registered successfully";
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Failed in registering user", e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during user registration", e);
        }
    }

    public LoginResponse login(LoginRequest loginrequest) {
        String identifier = loginrequest.getIdentifier();
        String password = loginrequest.getPassword();
        Users users;
        if (identifier.contains("@")) {
            users = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("Invalid email or userid"));
        } else {
            // userId login
            users = userRepository.findByUsername(identifier)
                    .orElseThrow(() -> new RuntimeException("Invalid email or userid"));
        }
        if (!passwordEncoder.matches(password, users.getPassword())) {
            throw new RuntimeException("Invalid credendials");
        }
        String token = jwtUtil.generateToken(users.getUserId());

        return new LoginResponse(token, users.getUsername(), users.getEmail());
    }
}
