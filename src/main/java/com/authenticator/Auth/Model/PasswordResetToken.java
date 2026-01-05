package com.authenticator.Auth.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "password_reset_token", schema = "Auth_Cred")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name ="id")
    private String id;

    @Column(name="token",unique = true, nullable = false)
    private String token;

    @Column(unique = true, nullable = false)
    private LocalDateTime expiryTime;

    @Column(name ="user_id")
    private String user_id;


    private boolean used;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
