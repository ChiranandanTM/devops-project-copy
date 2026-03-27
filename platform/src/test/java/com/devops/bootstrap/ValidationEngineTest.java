package com.devops.bootstrap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devops.bootstrap.platform.ServiceBootstrapRequest;
import com.devops.bootstrap.validation.ValidationEngine;

/**
 * Unit tests for ValidationEngine
 */
@DisplayName("ValidationEngine Tests")
class ValidationEngineTest {
    
    private ValidationEngine validationEngine;
    
    @BeforeEach
    void setUp() {
        this.validationEngine = new ValidationEngine();
    }
    
    @Test
    @DisplayName("Should validate correct service bootstrap request")
    void testValidateValidRequest() {
        ServiceBootstrapRequest request = new ServiceBootstrapRequest(
            "user-service",
            "8081",
            "com.company",
            "PostgreSQL",
            "ghp_test123",
            "company-org"
        );
        
        assertTrue(validationEngine.validate(request));
        assertTrue(validationEngine.getErrors().isEmpty());
    }
    
    @Test
    @DisplayName("Should reject empty service name")
    void testValidateEmptyServiceName() {
        ServiceBootstrapRequest request = new ServiceBootstrapRequest(
            "",
            "8081",
            "com.company",
            "PostgreSQL",
            "ghp_test123",
            "company-org"
        );
        
        assertFalse(validationEngine.validate(request));
        assertFalse(validationEngine.getErrors().isEmpty());
    }
    
    @Test
    @DisplayName("Should reject invalid port number")
    void testValidateInvalidPort() {
        ServiceBootstrapRequest request = new ServiceBootstrapRequest(
            "user-service",
            "99999",
            "com.company",
            "PostgreSQL",
            "ghp_test123",
            "company-org"
        );
        
        assertFalse(validationEngine.validate(request));
    }
    
    @Test
    @DisplayName("Should reject invalid package name")
    void testValidateInvalidPackage() {
        ServiceBootstrapRequest request = new ServiceBootstrapRequest(
            "user-service",
            "8081",
            "InvalidPackage",
            "PostgreSQL",
            "ghp_test123",
            "company-org"
        );
        
        assertFalse(validationEngine.validate(request));
    }
    
    @Test
    @DisplayName("Should reject unsupported database type")
    void testValidateUnsupportedDatabase() {
        ServiceBootstrapRequest request = new ServiceBootstrapRequest(
            "user-service",
            "8081",
            "com.company",
            "Redis",
            "ghp_test123",
            "company-org"
        );
        
        assertFalse(validationEngine.validate(request));
    }
}
