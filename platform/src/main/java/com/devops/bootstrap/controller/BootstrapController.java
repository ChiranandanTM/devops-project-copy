package com.devops.bootstrap.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devops.bootstrap.platform.MicroserviceBootstrapPlatform;
import com.devops.bootstrap.platform.ServiceBootstrapRequest;

/**
 * REST Controller for the Bootstrap Platform
 * 
 * Endpoints:
 * - POST /api/bootstrap - Trigger microservice generation
 * - GET /health - Health check
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BootstrapController {
    
    private static final Logger logger = LoggerFactory.getLogger(BootstrapController.class);
    
    @Value("${bootstrap.output.path:/generated-microservices}")
    private String outputPath;
    
    @Value("${jenkins.url:http://localhost:8080}")
    private String jenkinsUrl;
    
    @Value("${jenkins.job.prefix:}")
    private String jenkinsJobPrefix;
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        logger.debug("Health check called");
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Microservice Bootstrap Platform");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Bootstrap endpoint - generates a new microservice
     */
    @PostMapping("/bootstrap")
    public ResponseEntity<?> bootstrap(@RequestBody ServiceBootstrapRequest request) {
        try {
            logger.info("Bootstrap request received for service: {}", request.getServiceName());
            
            MicroserviceBootstrapPlatform platform = 
                new MicroserviceBootstrapPlatform(outputPath);
            
            MicroserviceBootstrapPlatform.BootstrapResult result = 
                platform.bootstrap(request);
            
            logger.info("✓ Microservice generated successfully: {}", result.getProjectPath());
            
            // Add Jenkins information to response
            Map<String, Object> response = result.getSummary();
            String jenkinsLink = buildJenkinsLink(request.getServiceName());
            String jenkinsSetupGuide = buildJenkinsSetupGuide(request.getServiceName());
            
            response.put("jenkinsUrl", jenkinsUrl);
            response.put("jenkinsLink", jenkinsLink);
            response.put("jenkinsJobStatus", "Setup Required - Follow the setup guide below");
            response.put("jenkinsSetupGuide", jenkinsSetupGuide);
            response.put("jenkinsJobCreationSteps", new String[]{
                "1. Go to: " + jenkinsUrl + "/job/create",
                "2. Select 'Pipeline job' type",
                "3. Enter job name: " + request.getServiceName(),
                "4. Configure: Pipeline > Pipeline script from SCM",
                "5. SCM: Git repository URL (from repositoryUrl field)",
                "6. Script path: Jenkinsfile",
                "7. Save and run the job"
            });
            
            logger.info("Jenkins setup guide: {}", jenkinsSetupGuide);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
                
        } catch (MicroserviceBootstrapPlatform.BootstrapException e) {
            logger.error("Bootstrap failed", e);
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Bootstrap Failed");
            error.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            logger.error("Unexpected error during bootstrap", e);
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal Server Error");
            error.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Info endpoint - provides platform information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> info() {
        Map<String, String> info = new HashMap<>();
        info.put("name", "Microservice Bootstrap Automation Platform");
        info.put("version", "1.0.0");
        info.put("description", "Generate production-ready microservices automatically");
        info.put("outputPath", outputPath);
        info.put("jenkinsUrl", jenkinsUrl);
        
        return ResponseEntity.ok(info);
    }
    
    /**
     * Builds the Jenkins job link for a microservice
     * Format: {jenkinsUrl}/job/{prefix}{serviceName}
     */
    private String buildJenkinsLink(String serviceName) {
        String baseUrl = jenkinsUrl.endsWith("/") ? jenkinsUrl : jenkinsUrl + "/";
        String prefix = (jenkinsJobPrefix != null && !jenkinsJobPrefix.isEmpty()) 
            ? jenkinsJobPrefix : "";
        return baseUrl + "job/" + prefix + serviceName;
    }
    
    /**
     * Builds a documentation link for Jenkins job setup
     * Points to the New Job creation page (stable, no plugin UI issues)
     */
    private String buildJenkinsSetupGuide(String serviceName) {
        String baseUrl = jenkinsUrl.endsWith("/") ? jenkinsUrl : jenkinsUrl + "/";
        return baseUrl + "view/all/newJob";
    }
}
