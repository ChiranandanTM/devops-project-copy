package com.devops.bootstrap.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devops.bootstrap.platform.ServiceBootstrapRequest;

/**
 * ValidationEngine validates ServiceBootstrapRequest inputs against
 * business rules, naming conventions, and system constraints.
 * 
 * Performs comprehensive validation including:
 * - Input presence and format validation
 * - Naming convention compliance
 * - Port range and availability checks
 * - Package naming validation
 * - Database type support validation
 */
public class ValidationEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(ValidationEngine.class);
    
    // Validation Constants
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;
    private static final Pattern SERVICE_NAME_PATTERN = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");
    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("^[a-z]([a-z0-9])*(\\.([a-z0-9])+)*$");
    private static final Set<String> SUPPORTED_DATABASES = new HashSet<>(Arrays.asList(
            "PostgreSQL", "MySQL", "MongoDB", "MariaDB", "SQLServer", "Oracle"
    ));
    
    private final List<String> validationErrors;
    private final List<String> validationWarnings;
    
    public ValidationEngine() {
        this.validationErrors = new ArrayList<>();
        this.validationWarnings = new ArrayList<>();
    }
    
    /**
     * Validates a complete ServiceBootstrapRequest.
     * 
     * @param request the request to validate
     * @return true if validation passed (no errors), false otherwise
     */
    public boolean validate(ServiceBootstrapRequest request) {
        validationErrors.clear();
        validationWarnings.clear();
        
        validateServiceName(request.getServiceName());
        validatePort(request.getPort());
        validatePackageName(request.getPackageName());
        validateDatabaseType(request.getDatabaseType());
        validateGitHubCredentials(request);
        validateDescription(request.getDescription());
        
        if (!validationErrors.isEmpty()) {
            logger.error("Validation failed with {} errors", validationErrors.size());
            validationErrors.forEach(logger::error);
            return false;
        }
        
        if (!validationWarnings.isEmpty()) {
            logger.warn("Validation completed with {} warnings", validationWarnings.size());
            validationWarnings.forEach(logger::warn);
        }
        
        logger.info("✓ All validations passed for service: {}", request.getServiceName());
        return true;
    }
    
    /**
     * Validates service name:
     * - Not null or empty
     * - Matches lowercase hyphenated pattern
     * - Between 3 and 50 characters
     * - Does not contain reserved keywords
     */
    private void validateServiceName(String serviceName) {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            addError("Service name is required and cannot be empty");
            return;
        }
        
        serviceName = serviceName.trim();
        
        if (serviceName.length() < 3) {
            addError("Service name must be at least 3 characters long");
        }
        
        if (serviceName.length() > 50) {
            addError("Service name must not exceed 50 characters");
        }
        
        if (!SERVICE_NAME_PATTERN.matcher(serviceName).matches()) {
            addError("Service name must be lowercase with hyphens only (e.g., 'user-service'). " +
                     "Invalid pattern: " + serviceName);
        }
        
        if (isReservedKeyword(serviceName)) {
            addError("Service name is a reserved Java keyword: " + serviceName);
        }
    }
    
    /**
     * Validates port:
     * - Valid numeric range (1024-65535)
     * - Not a system port
     */
    private void validatePort(String port) {
        if (port == null || port.trim().isEmpty()) {
            addError("Port is required");
            return;
        }
        
        try {
            int portNum = Integer.parseInt(port.trim());
            
            if (portNum < MIN_PORT || portNum > MAX_PORT) {
                addError(String.format("Port must be between %d and %d, got %d", 
                        MIN_PORT, MAX_PORT, portNum));
            }
            
            // Check for common system ports
            if (isCommonSystemPort(portNum)) {
                addWarning("Port " + portNum + " is commonly used. Ensure it's available.");
            }
        } catch (NumberFormatException e) {
            addError("Port must be a valid integer, got: " + port);
        }
    }
    
    /**
     * Validates package name:
     * - Not null or empty
     * - Follows Java package naming conventions
     * - At least 2 levels (e.g., com.company)
     * - All lowercase with dots
     */
    private void validatePackageName(String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) {
            addError("Package name is required");
            return;
        }
        
        packageName = packageName.trim();
        
        if (!PACKAGE_NAME_PATTERN.matcher(packageName).matches()) {
            addError("Package name must follow Java conventions (e.g., 'com.company'). " +
                     "Invalid: " + packageName);
        }
        
        String[] parts = packageName.split("\\.");
        if (parts.length < 2) {
            addError("Package name must have at least two levels (e.g., 'com.company')");
        }
        
        for (String part : parts) {
            if (isReservedKeyword(part)) {
                addError("Package contains reserved Java keyword: " + part);
            }
        }
    }
    
    /**
     * Validates database type against supported databases
     */
    private void validateDatabaseType(String databaseType) {
        if (databaseType == null || databaseType.trim().isEmpty()) {
            addError("Database type is required");
            return;
        }
        
        databaseType = databaseType.trim();
        
        if (!SUPPORTED_DATABASES.contains(databaseType)) {
            addError("Unsupported database type: " + databaseType + 
                     ". Supported: " + SUPPORTED_DATABASES);
        }
    }
    
    /**
     * Validates GitHub credentials:
     * - Token is not null or empty
     * - Owner is not null or empty
     * - Token is reasonably formatted
     */
    private void validateGitHubCredentials(ServiceBootstrapRequest request) {
        String token = request.getGitHubToken();
        String owner = request.getGitHubOwner();

        // GitHub credentials are optional — if no token, GitHub repo creation is skipped
        if (token != null && !token.trim().isEmpty()) {
            if (owner == null || owner.trim().isEmpty()) {
                addError("GitHub owner (user or organization) is required when a GitHub token is provided");
            }
            if (!token.startsWith("ghp_") && !token.startsWith("github_pat_")) {
                addWarning("GitHub token does not appear to be a valid personal access token");
            }
        }
    }
    
    /**
     * Validates description if provided
     */
    private void validateDescription(String description) {
        if (description != null && description.length() > 500) {
            addWarning("Description is very long (" + description.length() + " chars)");
        }
    }
    
    /**
     * Checks if a string is a reserved Java keyword
     */
    private boolean isReservedKeyword(String word) {
        Set<String> keywords = new HashSet<>(Arrays.asList(
                "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
                "class", "const", "continue", "default", "do", "double", "else", "enum",
                "extends", "final", "finally", "float", "for", "goto", "if", "implements",
                "import", "instanceof", "int", "interface", "long", "native", "new",
                "package", "private", "protected", "public", "return", "short", "static",
                "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while"
        ));
        return keywords.contains(word.toLowerCase());
    }
    
    /**
     * Checks if port is commonly used by system services
     */
    private boolean isCommonSystemPort(int port) {
        return (port >= 1 && port <= 1023) ||  // System reserved ports
               port == 3306 ||                   // MySQL
               port == 5432 ||                   // PostgreSQL
               port == 27017 ||                  // MongoDB
               port == 6379 ||                   // Redis
               port == 8080 ||                   // Common HTTP
               port == 8443;                     // Common HTTPS
    }
    
    // Error Management
    
    private void addError(String error) {
        validationErrors.add(error);
        logger.error("✗ Validation error: {}", error);
    }
    
    private void addWarning(String warning) {
        validationWarnings.add(warning);
        logger.warn("⚠ Validation warning: {}", warning);
    }
    
    public List<String> getErrors() {
        return new ArrayList<>(validationErrors);
    }
    
    public List<String> getWarnings() {
        return new ArrayList<>(validationWarnings);
    }
    
    public boolean hasErrors() {
        return !validationErrors.isEmpty();
    }
    
    public boolean hasWarnings() {
        return !validationWarnings.isEmpty();
    }
}
