package com.goaleaf.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Profile("dev")
public class DevConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/").allowedOrigins(SecurityConstants.CLIENT_URL);
                registry.addMapping("/api/comments").allowedOrigins(SecurityConstants.CLIENT_URL);
                registry.addMapping("/api/habits").allowedOrigins(SecurityConstants.CLIENT_URL);
                registry.addMapping("/api/members").allowedOrigins(SecurityConstants.CLIENT_URL);
                registry.addMapping("/api/notifications").allowedOrigins(SecurityConstants.CLIENT_URL);
                registry.addMapping("/api/posts").allowedOrigins(SecurityConstants.CLIENT_URL);
                registry.addMapping("/api/tasks").allowedOrigins(SecurityConstants.CLIENT_URL);
                registry.addMapping("/api/users").allowedOrigins(SecurityConstants.CLIENT_URL);
                registry.addMapping("/api/stats").allowedOrigins("*");

            }
        };
    }

}