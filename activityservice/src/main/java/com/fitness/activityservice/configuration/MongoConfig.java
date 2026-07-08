package com.fitness.activityservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        // In a real application with security, you would get the current user from the SecurityContext.
        // For example, using Spring Security: return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication().getName());
        return () -> Optional.of("system"); // Placeholder for the current user
    }
}
