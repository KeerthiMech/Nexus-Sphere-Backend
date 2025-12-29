package com.authenticator.UserProfile.config;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.authenticator.UserProfile.repository",   // repositories for profile schema
        entityManagerFactoryRef = "profileEntityManagerFactory",
        transactionManagerRef = "profileTransactionManager"
)
@EntityScan(basePackages = "com.authenticator.UserProfile.Model") // entities for profile schema
public class ProfileDBConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.profile")
    public DataSource profileDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "profileEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean profileEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("profileDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);

        return builder
                .dataSource(dataSource)
                .packages("com.authenticator.UserProfile.Model") // scan entities
                .persistenceUnit("profilePU")
                .properties(properties)
                .build();
    }

    @Bean(name = "profileTransactionManager")
    public PlatformTransactionManager profileTransactionManager(
            @Qualifier("profileEntityManagerFactory") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}