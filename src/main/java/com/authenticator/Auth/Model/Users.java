package com.authenticator.Auth.Model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name ="users", schema = "Auth_Cred")
@Data
public class Users {

    @Id
    @Column(name ="userId")
    private String userId;

    @Column(name ="username", unique = true, nullable = false)
    private String username;

    @Column(name ="password", nullable = false)
    private String password;

    @Column(name = "email" ,unique = true, nullable = false)
    private String email;

    private LocalDateTime createdAt;
}
