package com.devops.bootstrap.platform;

/**
 * ServiceModuleConfiguration contains derived configuration values
 * computed from the ServiceBootstrapRequest and platform defaults.
 * 
 * This class provides computed properties like class names, file paths,
 * Maven coordinates, and other derived values needed during generation.
 */
public class ServiceModuleConfiguration {
    
    private final ServiceBootstrapRequest request;
    private final String timestamp;
    private final String generatedPath;
    
    public ServiceModuleConfiguration(ServiceBootstrapRequest request, String generatedPath) {
        this.request = request;
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.generatedPath = generatedPath;
    }
    
    // Computed Properties
    
    /**
     * Returns the service name in PascalCase for Java class naming
     * e.g., "user-service" -> "UserService"
     */
    public String getServiceNamePascalCase() {
        return toPascalCase(request.getServiceName());
    }
    
    /**
     * Returns the service name in camelCase
     * e.g., "user-service" -> "userService"
     */
    public String getServiceNameCamelCase() {
        return toCamelCase(request.getServiceName());
    }
    
    /**
     * Returns the package path
     * e.g., "com.company" -> "com/company"
     */
    public String getPackagePath() {
        return request.getPackageName().replace(".", "/");
    }
    
    /**
     * Returns the full package for the service
     * e.g., "com.company" + "user-service" -> "com.company.userservice"
     */
    public String getFullPackage() {
        return request.getPackageName() + "." + request.getServiceName().replace("-", "").toLowerCase();
    }
    
    /**
     * Returns the full package path for file system
     */
    public String getFullPackagePath() {
        return getFullPackage().replace(".", "/");
    }
    
    /**
     * Returns the Maven GroupId
     */
    public String getMavenGroupId() {
        return request.getPackageName();
    }
    
    /**
     * Returns the Maven ArtifactId (service name with dashes)
     */
    public String getMavenArtifactId() {
        return request.getServiceName();
    }
    
    /**
     * Returns the main application class name
     * e.g., "UserServiceApplication"
     */
    public String getApplicationClassName() {
        return getServiceNamePascalCase() + "Application";
    }
    
    /**
     * Returns the repository name
     */
    public String getRepositoryClassName() {
        return getServiceNamePascalCase() + "Repository";
    }
    
    /**
     * Returns the service/business logic class name
     */
    public String getServiceClassName() {
        return getServiceNamePascalCase() + "Service";
    }
    
    /**
     * Returns the REST controller class name
     */
    public String getControllerClassName() {
        return getServiceNamePascalCase() + "Controller";
    }
    
    /**
     * Returns the entity class name
     */
    public String getEntityClassName() {
        return getServiceNamePascalCase() + "Entity";
    }
    
    /**
     * Returns the DTO class name
     */
    public String getDtoClassName() {
        return getServiceNamePascalCase() + "DTO";
    }
    
    /**
     * Returns the Docker image name
     */
    public String getDockerImageName() {
        return request.getPackageName().replace(".", "-").toLowerCase() + 
               "/" + request.getServiceName();
    }
    
    /**
     * Returns the Kubernetes deployment name
     */
    public String getK8sDeploymentName() {
        return request.getServiceName().toLowerCase();
    }
    
    // Utility Methods
    
    private String toPascalCase(String input) {
        if (input == null || input.isEmpty()) return "";
        String[] parts = input.split("-");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(part.substring(0, 1).toUpperCase())
                      .append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
    
    private String toCamelCase(String input) {
        String pascalCase = toPascalCase(input);
        if (pascalCase.isEmpty()) return "";
        return pascalCase.substring(0, 1).toLowerCase() + pascalCase.substring(1);
    }
    
    // Getters
    
    public ServiceBootstrapRequest getRequest() {
        return request;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public String getGeneratedPath() {
        return generatedPath;
    }
    
    @Override
    public String toString() {
        return "ServiceModuleConfiguration{" +
                "applicationClass=" + getApplicationClassName() +
                ", package=" + getFullPackage() +
                ", artifactId=" + getMavenArtifactId() +
                '}';
    }
}
