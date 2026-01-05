package com.authenticator.Auth.Service;


import com.authenticator.Auth.Model.PasswordResetToken;
import com.authenticator.Auth.Model.Users;
import com.authenticator.Auth.repository.PasswordResetTokenRepository;
import com.authenticator.Auth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.frontend.reset-url}")
    private String resetUrl;

    @Value("${app.reset-token.expiry-minutes}")
    private long expiryMinutes;


    public void forgotpasswordservice(String identifier) {
        // validate input
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Identifier (email or username) is required");
        }

        // normalize and trim
        String id = identifier.trim();
        Optional<Users> useropt = Optional.empty();

        try {
            if (id.contains("@")) {
                // common to store emails lowercase
                useropt = userRepository.findByEmail(id.toLowerCase());
            } else {
                useropt = userRepository.findByUsername(id);
            }

            // fallback: if primary lookup didn't find a user, try the other field
            if (useropt.isEmpty()) {
                // try alternate lookup to ensure SQL parameter is tested correctly
                if (id.contains("@")) {
                    useropt = userRepository.findByUsername(id);
                } else {
                    useropt = userRepository.findByEmail(id.toLowerCase());
                }
            }
        } catch (DataAccessException dae) {
            throw new RuntimeException("Database error while looking up user for identifier: " + id, dae);
        }

        Users user = useropt.orElseThrow(() ->
                new EntityNotFoundException("User not found for identifier: " + id)
        );

        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser_id(user.getUserId());
        passwordResetToken.setExpiryTime(LocalDateTime.now().plusMinutes(expiryMinutes));
        passwordResetToken.setUsed(false);

        passwordResetTokenRepository.save(passwordResetToken);

        String resetLink = resetUrl + "?token=" + token;
        String emailBody = "Click the below link to reset your password: " + resetLink;
        emailService.sendresetEmail(user.getEmail(), "Password Reset Request", emailBody);
    }

    @Transactional
    public void resetpasswordservice(String token, String newPassword) {

        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("Reset token is required");
        }
        if (!StringUtils.hasText(newPassword)) {
            throw new IllegalArgumentException("New password is required");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid password reset token"));

        if (resetToken.isUsed()) {
            throw new IllegalStateException("Reset token already used");
        }

        if (resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Reset token expired");
        }

        String userId = resetToken.getUser_id();
        if (!StringUtils.hasText(userId)) {
            throw new EntityNotFoundException("User associated with token not found");
        }

        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}

