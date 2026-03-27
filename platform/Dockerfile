# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Create non-root user for security
#RUN addgroup -S appgroup && adduser -S appuser -G appgroup
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Copy JAR from builder stage
#COPY --from=builder /app/target/bootstrap-platform-1.0.0.jar app.jar
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD java -version || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
