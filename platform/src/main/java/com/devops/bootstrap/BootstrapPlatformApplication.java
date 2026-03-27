package com.devops.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Application for Microservice Bootstrap Automation Platform
 * 
 * REST API Entry Points:
 * - POST /api/bootstrap - Generate a new microservice
 * - GET /health - Service health check
 */
@SpringBootApplication
public class BootstrapPlatformApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(BootstrapPlatformApplication.class);
    
    public static void main(String[] args) {
        SpringApplication.run(BootstrapPlatformApplication.class, args);
        logger.info("✓ Microservice Bootstrap Platform started successfully");
    }
}
