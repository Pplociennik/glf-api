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
                registry.addMapping("/").allowedOrigins("https://www.goaleaf.com");
                registry.addMapping("/api/comments").allowedOrigins("https://www.goaleaf.com");
                registry.addMapping("/api/habits").allowedOrigins("https://www.goaleaf.com");
                registry.addMapping("/api/members").allowedOrigins("https://www.goaleaf.com");
                registry.addMapping("/api/notifications").allowedOrigins("https://www.goaleaf.com");
                registry.addMapping("/api/posts").allowedOrigins("https://www.goaleaf.com");
                registry.addMapping("/api/tasks").allowedOrigins("https://www.goaleaf.com");
                registry.addMapping("/api/users").allowedOrigins("https://www.goaleaf.com");
                registry.addMapping("/api/stats").allowedOrigins("*");

            }
        };
    }

}