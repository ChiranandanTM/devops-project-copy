package com.devops.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.devops.bootstrap.platform.ServiceBootstrapRequest;
import com.devops.bootstrap.platform.ServiceModuleConfiguration;

/**
 * Unit tests for ServiceModuleConfiguration
 */
@DisplayName("ServiceModuleConfiguration Tests")
class ServiceModuleConfigurationTest {
    
    @Test
    @DisplayName("Should convert service name to PascalCase")
    void testServiceNamePascalCase() {
        ServiceBootstrapRequest request = new ServiceBootstrapRequest(
            "user-service",
            "8081",
            "com.company",
            "PostgreSQL",
            "ghp_test123",
            "company-org"
        );
        
        ServiceModuleConfiguration config = new ServiceModuleConfiguration(request, "/test/path");
        
        assertEquals("UserService", config.getServiceNamePascalCase());
    }
    
    @Test
    @DisplayName("Should convert service name to camelCase")
    void testServiceNameCamelCase() {
        ServiceBootstrapRequest request = new ServiceBootstrapRequest(
            "user-service",
            "8081",
            "com.company",
            "PostgreSQL",
            "ghp_test123",
            "company-org"
        );
        
        ServiceModuleConfiguration config = new ServiceModuleConfiguration(request, "/test/path");
        
        assertEquals("userService", config.getServiceNameCamelCase());
    }
    
    @Test
    @DisplayName("Should generate correct full package name")
    void testFullPackageName() {
        ServiceBootstrapRequest request = new ServiceBootstrapRequest(
            "user-service",
            "8081",
            "com.company",
            "PostgreSQL",
            "ghp_test123",
            "company-org"
        );
        
        ServiceModuleConfiguration config = new ServiceModuleConfiguration(request, "/test/path");
        
        assertEquals("com.company.userservice", config.getFullPackage());
    }
    
    @Test
    @DisplayName("Should generate correct application class name")
    void testApplicationClassName() {
        ServiceBootstrapRequest request = new ServiceBootstrapRequest(
            "user-service",
            "8081",
            "com.company",
            "PostgreSQL",
            "ghp_test123",
            "company-org"
        );
        
        ServiceModuleConfiguration config = new ServiceModuleConfiguration(request, "/test/path");
        
        assertEquals("UserServiceApplication", config.getApplicationClassName());
    }
}
