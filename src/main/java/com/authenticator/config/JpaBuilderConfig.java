package com.authenticator.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {
                "com.authenticator.Auth.repository",
                "com.authenticator.UserProfile.repository"
        }
)
@EntityScan(basePackages = {
        "com.authenticator.Auth.Model",
        "com.authenticator.UserProfile.Model"
})
public class JpaBuilderConfig {
    // No manual beans needed, Spring Boot auto-configures everything
}