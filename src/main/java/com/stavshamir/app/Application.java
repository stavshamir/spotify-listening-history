package com.stavshamir.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ComponentScan
public class Application {

    @Value("${allowed-origins.listening-history.get}")
    private String[] listeningHistoryGetAllowedOrigins;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/listening-history/get").allowedOrigins(listeningHistoryGetAllowedOrigins);
                registry.addMapping("/listening-history/most-played").allowedOrigins(listeningHistoryGetAllowedOrigins);
                registry.addMapping("/authorize").allowedOrigins(listeningHistoryGetAllowedOrigins);
                registry.addMapping("/authorize/code").allowedOrigins(listeningHistoryGetAllowedOrigins);
            }
        };
    }

}

