package com.authenticator.Service;


import com.authenticator.Model.PasswordResetToken;
import com.authenticator.Model.Users;
import com.authenticator.repository.PasswordResetTokenRepository;
import com.authenticator.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        passwordResetToken.setUsers(user);
        passwordResetToken.setExpiryTime(LocalDateTime.now().plusMinutes(expiryMinutes));
        passwordResetToken.setUsed(false);

        passwordResetTokenRepository.save(passwordResetToken);

        String resetLink = resetUrl + "?token=" + token;
        String emailBody = "Click the below link to reset your password: " + resetLink;
        emailService.sendresetEmail(user.getEmail(), "Password Reset Request", emailBody);
    }

    @Transactional
    public void resetpasswordservice(String token, String newPassword) {
        // validate inputs
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Reset token is required");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password is required");
        }

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid password reset token"));

        if (passwordResetToken.isUsed() || passwordResetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Password reset token is either used or expired");
        }

        // derive userId from token
        String userId = null;
        if (passwordResetToken.getUsers() != null && passwordResetToken.getUsers().getUserId() != null) {
            userId = passwordResetToken.getUsers().getUserId();
        } else {
            throw new EntityNotFoundException("User associated with token not found");
        }

        // encode new password and perform an update via JPQL to ensure a direct SQL update
        String encodedPassword = passwordEncoder.encode(newPassword);
        try {
            int updated = entityManager.createQuery("UPDATE Users u SET u.password = :pwd WHERE u.userId = :id")
                    .setParameter("pwd", encodedPassword)
                    .setParameter("id", userId)
                    .executeUpdate();

            if (updated == 0) {
                throw new RuntimeException("Failed to update password for userId: " + userId);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Database error while updating password for userId: " + userId, ex);
        }

        // mark token as used
        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);
    }
}
