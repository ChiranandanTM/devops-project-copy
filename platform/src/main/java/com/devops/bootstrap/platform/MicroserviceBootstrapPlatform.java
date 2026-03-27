package com.devops.bootstrap.platform;

import com.devops.bootstrap.generator.*;
import com.devops.bootstrap.integration.GitHubIntegration;
import com.devops.bootstrap.validation.ValidationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * MicroserviceBootstrapPlatform is the main orchestrator that coordinates
 * all components to generate a complete production-ready microservice.
 * 
 * Workflow:
 * 1. Validate user input
 * 2. Create service configuration
 * 3. Generate boilerplate (files, code, configs)
 * 4. Create GitHub repository
 * 5. Generate Docker configuration
 * 6. Generate Jenkins CI/CD pipeline
 * 7. Return complete, deployment-ready microservice
 * 
 * This is the entry point for the entire platform.
 */
public class MicroserviceBootstrapPlatform {
    
    private static final Logger logger = LoggerFactory.getLogger(MicroserviceBootstrapPlatform.class);
    
    private final String baseOutputPath;
    private final ValidationEngine validationEngine;
    private FileSystemGenerator fileSystemGenerator;
    private TemplateEngine templateEngine;
    private DockerGenerator dockerGenerator;
    private JenkinsPipelineGenerator pipelineGenerator;
    private GitHubIntegration githubIntegration;
    
    public MicroserviceBootstrapPlatform(String baseOutputPath) {
        this.baseOutputPath = baseOutputPath;
        this.validationEngine = new ValidationEngine();
        this.initializeGenerators();
    }
    
    /**
     * Initializes all required generators and integrations
     */
    private void initializeGenerators() {
        this.templateEngine = new TemplateEngine();
        this.fileSystemGenerator = new FileSystemGenerator(baseOutputPath, templateEngine);
        this.dockerGenerator = new DockerGenerator(templateEngine);
        this.pipelineGenerator = new JenkinsPipelineGenerator(templateEngine);
    }
    
    /**
     * Main entry point: generates complete microservice from request
     * 
     * @param request the ServiceBootstrapRequest with user input
     * @return BootstrapResult containing all generated artifacts and info
     * @throws BootstrapException if bootstrap process fails
     */
    public BootstrapResult bootstrap(ServiceBootstrapRequest request) throws BootstrapException {
        try {
            logger.info("╔════════════════════════════════════════════════════════════╗");
            logger.info("║  MICROSERVICE BOOTSTRAP AUTOMATION PLATFORM - EXECUTION    ║");
            logger.info("╚════════════════════════════════════════════════════════════╝\n");
            
            // Step 1: Validate Input
            logger.info("\n[STEP 1] Validating Input Parameters...");
            if (!validationEngine.validate(request)) {
                throw new BootstrapException("Input validation failed: " + 
                    String.join("; ", validationEngine.getErrors()));
            }
            
            // Step 2: Create Configuration
            logger.info("\n[STEP 2] Creating Service Configuration...");
            String projectPath = Paths.get(baseOutputPath, request.getServiceName()).toString();
            ServiceModuleConfiguration config = new ServiceModuleConfiguration(request, projectPath);
            logger.info("✓ Configuration created: {}", config);
            
            // Step 3: Generate Boilerplate
            logger.info("\n[STEP 3] Generating Boilerplate Files...");
            String generatedProjectPath = fileSystemGenerator.generateProject(config);
            logger.info("✓ Project structure generated");
            
            // Step 4: Generate Docker Configuration
            logger.info("\n[STEP 4] Generating Docker Configuration...");
            dockerGenerator.generateDockerConfiguration(generatedProjectPath, config);
            logger.info("✓ Docker configuration generated");
            
            // Step 5: Generate Jenkins Pipeline
            logger.info("\n[STEP 5] Generating Jenkins CI/CD Pipeline...");
            pipelineGenerator.generatePipelineConfigurations(generatedProjectPath, config);
            logger.info("✓ Jenkins pipeline generated");
            
            // Step 6: Create GitHub Repository (if credentials provided)
            String repositoryUrl = null;
            if (request.getGitHubToken() != null && !request.getGitHubToken().isEmpty()) {
                logger.info("\n[STEP 6] Setting Up GitHub Repository...");
                this.githubIntegration = new GitHubIntegration(request.getGitHubToken(), 
                                                                request.getGitHubOwner());
                repositoryUrl = createGitHubRepository(request, generatedProjectPath);
                logger.info("✓ GitHub repository created: {}", repositoryUrl);
            }
            
            // Step 7: Validation and Readiness Check
            logger.info("\n[STEP 7] Validating Deployment Readiness...");
            validateDeploymentReadiness(generatedProjectPath);
            logger.info("✓ All validations passed");
            
            // Compile Results
            logger.info("\n╔════════════════════════════════════════════════════════════╗");
            logger.info("║  ✓ MICROSERVICE BOOTSTRAP COMPLETED SUCCESSFULLY            ║");
            logger.info("╚════════════════════════════════════════════════════════════╝");
            
            return new BootstrapResult(
                config,
                generatedProjectPath,
                repositoryUrl,
                createSummaryMap(config, generatedProjectPath, repositoryUrl)
            );
            
        } catch (Exception e) {
            logger.error("Bootstrap process failed", e);
            throw new BootstrapException("Microservice bootstrap failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Creates GitHub repository and performs initial setup
     */
    private String createGitHubRepository(ServiceBootstrapRequest request, 
                                        String projectPath) throws Exception {
        String repoName = request.getGitHubRepository();
        String description = request.getDescription() != null ? 
            request.getDescription() : "Microservice generated by Bootstrap Platform";
        
        // Create repository
        String repoUrl = githubIntegration.createRepository(repoName, description, false);
        
        // Initialize local git
        initializeLocalGit(projectPath, repoUrl);
        
        // Create develop branch
        String[] repoParts = repoUrl.split("/");
        String repoNameOnly = repoParts[repoParts.length - 1].replace(".git", "");
        githubIntegration.createBranch(repoNameOnly, "develop", "main");
        
        // Configure webhook for CI/CD
        String jenkinsWebhookUrl = "http://jenkins-server/github-webhook/"; // Configurable
        try {
            githubIntegration.configureWebhook(repoNameOnly, jenkinsWebhookUrl);
        } catch (Exception e) {
            logger.warn("Could not configure webhook: {}", e.getMessage());
        }
        
        // Configure branch protection
        try {
            githubIntegration.configureBranchProtection(repoNameOnly, "main");
        } catch (Exception e) {
            logger.warn("Could not configure branch protection: {}", e.getMessage());
        }
        
        return repoUrl;
    }
    
    /**
     * Initializes local git repository
     */
    private void initializeLocalGit(String projectPath, String repoUrl) throws Exception {
        logger.info("Initializing git repository...");
        
        ProcessBuilder pb = new ProcessBuilder("git", "init");
        pb.directory(new java.io.File(projectPath));
        pb.start().waitFor();
        
        pb = new ProcessBuilder("git", "add", ".");
        pb.directory(new java.io.File(projectPath));
        pb.start().waitFor();
        
        pb = new ProcessBuilder("git", "commit", "-m", "Initial commit - Generated by Bootstrap Platform");
        pb.directory(new java.io.File(projectPath));
        pb.start().waitFor();
        
        pb = new ProcessBuilder("git", "remote", "add", "origin", repoUrl);
        pb.directory(new java.io.File(projectPath));
        pb.start().waitFor();
        
        logger.info("✓ Git repository initialized");
    }
    
    /**
     * Validates deployment readiness
     */
    private void validateDeploymentReadiness(String projectPath) throws IOException {
        Path path = Paths.get(projectPath);
        
        // Check for critical files
        String[] criticalFiles = {
            "pom.xml",
            "Dockerfile",
            "docker-compose.yml",
            "Jenkinsfile",
            "src/main/java",
            "src/test/java",
            "src/main/resources"
        };
        
        for (String file : criticalFiles) {
            Path filePath = path.resolve(file);
            if (!Files.exists(filePath)) {
                throw new IOException("Critical file missing: " + file);
            }
        }
        
        logger.info("✓ All critical files validated");
        
        // Validate Dockerfile
        validateDockerfile(projectPath);
        
        // Validate Jenkinsfile
        validateJenkinsfile(projectPath);
    }
    
    /**
     * Validates Dockerfile syntax
     */
    private void validateDockerfile(String projectPath) throws IOException {
        Path dockerfilePath = Paths.get(projectPath, "Dockerfile");
        String content = Files.readString(dockerfilePath, StandardCharsets.UTF_8);
        
        if (!content.contains("FROM") || !content.contains("ENTRYPOINT")) {
            throw new IOException("Dockerfile validation failed: missing required directives");
        }
        
        logger.info("✓ Dockerfile validated");
    }
    
    /**
     * Validates Jenkinsfile syntax
     */
    private void validateJenkinsfile(String projectPath) throws IOException {
        Path jenkinsfilePath = Paths.get(projectPath, "Jenkinsfile");
        String content = Files.readString(jenkinsfilePath, StandardCharsets.UTF_8);
        
        if (!content.contains("pipeline") || !content.contains("stages")) {
            throw new IOException("Jenkinsfile validation failed: missing pipeline structure");
        }
        
        logger.info("✓ Jenkinsfile validated");
    }
    
    /**
     * Creates a summary map of generated artifacts
     */
    private Map<String, Object> createSummaryMap(ServiceModuleConfiguration config,
                                                  String projectPath,
                                                  String repositoryUrl) {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("serviceName", config.getRequest().getServiceName());
        summary.put("port", config.getRequest().getPort());
        summary.put("package", config.getFullPackage());
        summary.put("projectPath", projectPath);
        summary.put("repositoryUrl", repositoryUrl);
        summary.put("dockerImage", config.getDockerImageName());
        summary.put("apiBaseUrl", "http://localhost:" + config.getRequest().getPort() + 
                                  "/api/v1/" + config.getRequest().getServiceName());
        summary.put("healthCheckEndpoint", "/health");
        summary.put("generatedFiles", new String[]{
            "pom.xml",
            "Dockerfile",
            "docker-compose.yml",
            "Jenkinsfile",
            "Src/main/java, src/test/java, src/main/resources"
        });
        
        return summary;
    }
    
    /**
     * Result class containing bootstrap output
     */
    public static class BootstrapResult {
        private final ServiceModuleConfiguration config;
        private final String projectPath;
        private final String repositoryUrl;
        private final Map<String, Object> summary;
        
        public BootstrapResult(ServiceModuleConfiguration config, String projectPath,
                             String repositoryUrl, Map<String, Object> summary) {
            this.config = config;
            this.projectPath = projectPath;
            this.repositoryUrl = repositoryUrl;
            this.summary = summary;
        }
        
        public ServiceModuleConfiguration getConfig() { return config; }
        public String getProjectPath() { return projectPath; }
        public String getRepositoryUrl() { return repositoryUrl; }
        public Map<String, Object> getSummary() { return summary; }
        
        @Override
        public String toString() {
            return "BootstrapResult{" +
                    "serviceName='" + config.getRequest().getServiceName() + '\'' +
                    ", projectPath='" + projectPath + '\'' +
                    ", repositoryUrl='" + repositoryUrl + '\'' +
                    '}';
        }
    }
    
    /**
     * Custom exception for bootstrap errors
     */
    public static class BootstrapException extends Exception {
        public BootstrapException(String message) {
            super(message);
        }
        
        public BootstrapException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
