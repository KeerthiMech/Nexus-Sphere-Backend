package com.authenticator.Auth.repository;

import com.authenticator.Auth.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users,String> {
    Optional<Users> findByUsername (String username);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByUserId(String userId);
}
