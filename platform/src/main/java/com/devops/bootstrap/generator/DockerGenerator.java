package com.devops.bootstrap.generator;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devops.bootstrap.platform.ServiceModuleConfiguration;

/**
 * DockerGenerator creates Docker-related configuration files:
 * - Dockerfile (multi-stage build for optimization)
 * - docker-compose.yml (local development environment)
 * - .dockerignore (exclude unnecessary files)
 * 
 * Features:
 * - Multi-stage builds to minimize image size
 * - Alpine Linux base for production efficiency
 * - Health checks configured
 * - Environment variable support
 * - Logging volume mounts
 */
public class DockerGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(DockerGenerator.class);
    
    private final TemplateEngine templateEngine;
    
    public DockerGenerator(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
    
    /**
     * Generates Docker configuration files
     */
    public void generateDockerConfiguration(String projectPath, ServiceModuleConfiguration config) 
        throws IOException {
        logger.info("Generating Docker configuration");
        
        // Dockerfile
        String dockerfile = templateEngine.processTemplate(getDockerfileTemplate(config), config);
        writeFile(projectPath + "/Dockerfile", dockerfile);
        
        // docker-compose.yml
        String dockerCompose = templateEngine.processTemplate(getDockerComposeTemplate(config), config);
        writeFile(projectPath + "/docker-compose.yml", dockerCompose);
        
        // .dockerignore
        String dockerIgnore = getDockerigonoreTemplate();
        writeFile(projectPath + "/.dockerignore", dockerIgnore);
        
        logger.info("✓ Docker configuration generated");
    }
    
    /**
     * Multi-stage Dockerfile template
     */
    public String getDockerfileTemplate(ServiceModuleConfiguration config) {
        return "# Stage 1: Build\n" +
               "FROM maven:3.9.6-eclipse-temurin-17 AS builder\n\n" +
               "WORKDIR /build\n" +
               "COPY pom.xml .\n" +
               "RUN mvn dependency:resolve\n\n" +
               "COPY . .\n" +
               "RUN mvn clean package -DskipTests\n\n" +
               "# Stage 2: Runtime\n" +
               "FROM eclipse-temurin:${baseImageVersion}\n\n" +
               "LABEL maintainer=\"DevOps Team\"\n" +
               "LABEL description=\"${serviceName_pascal} - Microservice\"\n" +
               "LABEL version=\"1.0.0\"\n\n" +
               "WORKDIR /app\n\n" +
               "# Create non-root user for security\n" +
               "RUN groupadd -r appuser && useradd -r -g appuser appuser\n\n" +
               "# Copy JAR from build stage\n" +
               "COPY --from=builder /build/target/*.jar application.jar\n\n" +
               "# Create logs directory\n" +
               "RUN mkdir -p /app/logs && chown -R appuser:appuser /app\n\n" +
               "USER appuser\n\n" +
               "EXPOSE ${port}\n\n" +
               "# Health check\n" +
               "HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \\\n" +
               "    CMD curl -f http://localhost:${port}/api/v1/${serviceName}/health || exit 1\n\n" +
               "# Application entry point\n" +
               "ENTRYPOINT [\"java\", \"-jar\", \"application.jar\"]\n" +
               "CMD [\"--server.port=${port}\"]\n";
    }
    
    /**
     * docker-compose.yml for local development
     */
    public String getDockerComposeTemplate(ServiceModuleConfiguration config) {
        return "version: '3.9'\n\n" +
               "services:\n" +
               "  ${serviceName}:\n" +
               "    build:\n" +
               "      context: .\n" +
               "      dockerfile: Dockerfile\n" +
               "    container_name: ${serviceName}\n" +
               "    ports:\n" +
               "      - \"${port}:${port}\"\n" +
               "    environment:\n" +
               "      - SPRING_PROFILES_ACTIVE=dev\n" +
               "      - SERVER_PORT=${port}\n" +
               "      - DB_HOST=postgres\n" +
               "      - DB_PORT=5432\n" +
               "      - DB_NAME=${serviceName}\n" +
               "      - DB_USER=postgres\n" +
               "      - DB_PASSWORD=postgres\n" +
               "    volumes:\n" +
               "      - ./logs:/app/logs\n" +
               "      - ./target:/app/target\n" +
               "    depends_on:\n" +
               "      - postgres\n" +
               "    networks:\n" +
               "      - microservices\n" +
               "    healthcheck:\n" +
               "      test: [\"CMD\", \"curl\", \"-f\", \"http://localhost:${port}/api/v1/${serviceName}/health\"]\n" +
               "      interval: 30s\n" +
               "      timeout: 10s\n" +
               "      retries: 3\n" +
               "      start_period: 40s\n\n" +
               "  postgres:\n" +
               "    image: postgres:15-alpine\n" +
               "    container_name: postgres_${serviceName}\n" +
               "    environment:\n" +
               "      POSTGRES_DB: ${serviceName}\n" +
               "      POSTGRES_USER: postgres\n" +
               "      POSTGRES_PASSWORD: postgres\n" +
               "    ports:\n" +
               "      - \"5432:5432\"\n" +
               "    volumes:\n" +
               "      - postgres_data:/var/lib/postgresql/data\n" +
               "    networks:\n" +
               "      - microservices\n" +
               "    healthcheck:\n" +
               "      test: [\"CMD-SHELL\", \"pg_isready -U postgres\"]\n" +
               "      interval: 10s\n" +
               "      timeout: 5s\n" +
               "      retries: 5\n\n" +
               "volumes:\n" +
               "  postgres_data:\n" +
               "    driver: local\n\n" +
               "networks:\n" +
               "  microservices:\n" +
               "    driver: bridge\n";
    }
    
    /**
     * .dockerignore file
     */
    private String getDockerigonoreTemplate() {
        return "# Git\n" +
               ".git\n" +
               ".gitignore\n" +
               ".gitattributes\n\n" +
               "# IDE\n" +
               ".idea\n" +
               ".vscode\n" +
               ".DS_Store\n\n" +
               "# Maven\n" +
               ".mvn\n" +
               "mvnw\n" +
               "mvnw.cmd\n\n" +
               "# Build\n" +
               "target\n" +
               "build\n" +
               "dist\n\n" +
               "# Docker\n" +
               "Dockerfile\n" +
               "docker-compose.yml\n" +
               ".dockerignore\n\n" +
               "# CI/CD\n" +
               "Jenkinsfile\n" +
               ".github\n\n" +
               "# Logs\n" +
               "logs\n" +
               "*.log\n\n" +
               "# Documentation\n" +
               "*.md\n" +
               "README.md\n";
    }
    
    /**
     * Helper method to write file
     */
    private void writeFile(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes(StandardCharsets.UTF_8));
        logger.info("✓ Generated: {}", filePath);
    }
}
