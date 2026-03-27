# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:resolve

COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17

LABEL maintainer="DevOps Team"
LABEL description="EcommerceWebsite - Microservice"
LABEL version="1.0.0"

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy JAR from build stage
COPY --from=builder /build/target/*.jar application.jar

# Create logs directory
RUN mkdir -p /app/logs && chown -R appuser:appuser /app

USER appuser

EXPOSE 8094

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8094/api/v1/ecommerce-website/health || exit 1

# Application entry point
ENTRYPOINT ["java", "-jar", "application.jar"]
CMD ["--server.port=8094"]
