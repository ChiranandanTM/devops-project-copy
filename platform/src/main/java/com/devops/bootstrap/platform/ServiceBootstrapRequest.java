package com.devops.bootstrap.platform;

import java.util.Map;

/**
 * ServiceBootstrapRequest represents the complete input from a user
 * for generating a new microservice.
 * 
 * This is the primary model for capturing all configuration parameters
 * needed to scaffold a microservice automatically.
 */
public class ServiceBootstrapRequest {
    
    private String serviceName;           // e.g., "user-service"
    private String port;                   // e.g., "8081"
    private String packageName;            // e.g., "com.company"
    private String databaseType;           // e.g., "PostgreSQL", "MySQL", "MongoDB"
    private String gitHubToken;           // GitHub personal access token
    private String gitHubOwner;           // Repository owner (org or user)
    private String gitHubRepository;      // Repository name (auto-generated if null)
    private String description;           // Service description
    private String baseImageVersion;      // Docker base image version
    private Map<String, String> additionalProperties; // Extra configuration
    
    // Constructors
    public ServiceBootstrapRequest() {}
    
    public ServiceBootstrapRequest(String serviceName, String port, String packageName, 
                                   String databaseType, String gitHubToken, String gitHubOwner) {
        this.serviceName = serviceName;
        this.port = port;
        this.packageName = packageName;
        this.databaseType = databaseType;
        this.gitHubToken = gitHubToken;
        this.gitHubOwner = gitHubOwner;
        this.baseImageVersion = "17.0.6-alpine"; // Default Java version
    }
    
    // Getters and Setters
    public String getServiceName() {
        return serviceName;
    }
    
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public String getPort() {
        return port;
    }
    
    public void setPort(String port) {
        this.port = port;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String getDatabaseType() {
        return databaseType;
    }
    
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }
    
    public String getGitHubToken() {
        return gitHubToken;
    }
    
    public void setGitHubToken(String gitHubToken) {
        this.gitHubToken = gitHubToken;
    }
    
    public String getGitHubOwner() {
        return gitHubOwner;
    }
    
    public void setGitHubOwner(String gitHubOwner) {
        this.gitHubOwner = gitHubOwner;
    }
    
    public String getGitHubRepository() {
        return gitHubRepository != null ? gitHubRepository : serviceName;
    }
    
    public void setGitHubRepository(String gitHubRepository) {
        this.gitHubRepository = gitHubRepository;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getBaseImageVersion() {
        return baseImageVersion;
    }
    
    public void setBaseImageVersion(String baseImageVersion) {
        this.baseImageVersion = baseImageVersion;
    }
    
    public Map<String, String> getAdditionalProperties() {
        return additionalProperties;
    }
    
    public void setAdditionalProperties(Map<String, String> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
    
    @Override
    public String toString() {
        return "ServiceBootstrapRequest{" +
                "serviceName='" + serviceName + '\'' +
                ", port='" + port + '\'' +
                ", packageName='" + packageName + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", gitHubOwner='" + gitHubOwner + '\'' +
                ", baseImageVersion='" + baseImageVersion + '\'' +
                '}';
    }
}
