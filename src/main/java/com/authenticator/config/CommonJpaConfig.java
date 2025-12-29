package com.authenticator.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class CommonJpaConfig {

    /**
     * Create a JpaVendorAdapter (Hibernate) and configure showSql / generateDdl
     * by reading properties from the Environment. This avoids calling methods
     * that may not exist on JpaProperties across Spring Boot versions.
     */
    @Bean
    public JpaVendorAdapter jpaVendorAdapter(Environment env) {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

        // Set database type (adjust if you use a different DB)
        adapter.setDatabase(Database.MYSQL);

        // Read flags from application.properties / application.yml
        // spring.jpa.show-sql and spring.jpa.generate-ddl are standard properties
        String showSql = env.getProperty("spring.jpa.show-sql");
        String generateDdl = env.getProperty("spring.jpa.generate-ddl");

        adapter.setShowSql(Boolean.parseBoolean(showSql != null ? showSql : "false"));
        adapter.setGenerateDdl(Boolean.parseBoolean(generateDdl != null ? generateDdl : "false"));

        // Optionally set a specific dialect if you prefer:
        // adapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");

        return adapter;
    }

    /**
     * Build EntityManagerFactoryBuilder using the JpaVendorAdapter and JpaProperties.
     * JpaProperties provides the properties map used by the builder.
     */
    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(
            JpaVendorAdapter jpaVendorAdapter,
            JpaProperties jpaProperties,
            ObjectProvider<PersistenceUnitManager> persistenceUnitManager) {
        return new EntityManagerFactoryBuilder(
                jpaVendorAdapter,
                jpaProperties.getProperties(),
                persistenceUnitManager.getIfAvailable()
        );
    }
}
