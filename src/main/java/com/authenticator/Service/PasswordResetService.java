package com.authenticator.Service;


import com.authenticator.Model.PasswordResetToken;
import com.authenticator.Model.Users;
import com.authenticator.repository.PasswordResetTokenRepository;
import com.authenticator.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Value("${app.frontend.reset-url}")
    private String resetUrl;

    @Value("${app.reset-token.expiry-minutes}")
    private long expiryMinutes;


    public void forgotpasswordservice(String identifier) {
        Optional<Users> useropt;
        if(identifier.contains("@")) {
            useropt = userRepository.findByEmail(identifier);
        }else {
            useropt = userRepository.findByUsername(identifier);
        }
        if(useropt.isPresent()) {
            Users user = useropt.orElseThrow(() -> new EntityNotFoundException("User not found"));
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
    }

    public void resetpasswordservice(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("Invalid password reset token"));

        if(passwordResetToken.isUsed() || passwordResetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Password reset token is either used or expired");
        }

        Users user = userRepository.findByUserId(passwordResetToken.getUsers().getUserId()).orElseThrow(() -> new RuntimeException("User Not Found"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetToken.setUsed(true);
        passwordResetTokenRepository.save(passwordResetToken);



    }
}
