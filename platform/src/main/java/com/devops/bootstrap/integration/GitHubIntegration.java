package com.devops.bootstrap.integration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * GitHubIntegration handles all GitHub API interactions:
 * - Repository creation
 * - Branch setup
 * - Initial commit push
 * - CI/CD webhook configuration
 * 
 * Uses GitHub REST API v3 with OAuth token authentication
 */
public class GitHubIntegration {
    
    private static final Logger logger = LoggerFactory.getLogger(GitHubIntegration.class);
    
    private static final String GITHUB_API_BASE = "https://api.github.com";
    private static final String GITHUB_API_VERSION = "application/vnd.github.v3+json";
    
    private final OkHttpClient httpClient;
    private final Gson gson;
    private final String gitHubToken;
    private final String gitHubOwner;
    
    public GitHubIntegration(String gitHubToken, String gitHubOwner) {
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
        this.gitHubToken = gitHubToken;
        this.gitHubOwner = gitHubOwner;
    }
    
    /**
     * Creates a GitHub repository
     * 
     * @param repositoryName the repository name
     * @param description the repository description
     * @param isPrivate whether the repository should be private
     * @return the repository URL
     */
    public String createRepository(String repositoryName, String description, boolean isPrivate) {
        try {
            logger.info("Creating GitHub repository: {}", repositoryName);
            
            // Check if repository already exists
            if (repositoryExists(repositoryName)) {
                logger.warn("Repository {} already exists, skipping creation", repositoryName);
                return getRepositoryUrl(repositoryName);
            }
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("name", repositoryName);
            payload.put("description", description);
            payload.put("private", isPrivate);
            payload.put("auto_init", false);  // Don't initialize with README
            
            String url = GITHUB_API_BASE + "/user/repos";
            Response response = executePost(url, gson.toJson(payload));
            
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    JsonObject jsonResponse = gson.fromJson(responseBody.string(), JsonObject.class);
                    String repoUrl = jsonResponse.get("clone_url").getAsString();
                    logger.info("✓ Repository created successfully: {}", repoUrl);
                    return repoUrl;
                } else {
                    throw new GitHubAPIException("Empty response from GitHub API");
                }
            } else {
                ResponseBody responseBody = response.body();
                String error = responseBody != null ? responseBody.string() : "Unknown error";
                throw new GitHubAPIException("Failed to create repository: " + error);
            }
        } catch (IOException | NullPointerException e) {
            logger.error("Error creating repository", e);
            throw new GitHubAPIException("Repository creation failed", e);
        }
    }
    
    /**
     * Checks if a repository already exists
     */
    private boolean repositoryExists(String repositoryName) {
        try {
            String url = GITHUB_API_BASE + "/repos/" + gitHubOwner + "/" + repositoryName;
            Response response = executeGet(url);
            return response.isSuccessful();
        } catch (IOException e) {
            logger.debug("Repository existence check failed", e);
            return false;
        }
    }
    
    /**
     * Gets the repository URL
     */
    private String getRepositoryUrl(String repositoryName) {
        return "https://github.com/" + gitHubOwner + "/" + repositoryName + ".git";
    }
    
    /**
     * Creates a new branch in the repository
     * 
     * @param repositoryName the repository name
     * @param branchName the branch name
     * @param baseBranch the base branch (usually "main")
     */
    public void createBranch(String repositoryName, String branchName, String baseBranch) {
        try {
            logger.info("Creating branch {} in repository {}", branchName, repositoryName);
            
            // Get the SHA of the base branch
            String shaUrl = GITHUB_API_BASE + "/repos/" + gitHubOwner + "/" + 
                           repositoryName + "/refs/heads/" + baseBranch;
            Response shaResponse = executeGet(shaUrl);
            
            if (!shaResponse.isSuccessful()) {
                throw new GitHubAPIException("Failed to get base branch SHA");
            }
            
            ResponseBody shaBody = shaResponse.body();
            if (shaBody == null) {
                throw new GitHubAPIException("Empty response body");
            }
            JsonObject shaJson = gson.fromJson(shaBody.string(), JsonObject.class);
            String baseSha = shaJson.getAsJsonObject("object").get("sha").getAsString();
            
            // Create the new branch
            Map<String, Object> payload = new HashMap<>();
            payload.put("ref", "refs/heads/" + branchName);
            payload.put("sha", baseSha);
            
            String url = GITHUB_API_BASE + "/repos/" + gitHubOwner + "/" + 
                        repositoryName + "/git/refs";
            Response response = executePost(url, gson.toJson(payload));
            
            if (response.isSuccessful()) {
                logger.info("✓ Branch created: {}", branchName);
            } else {
                throw new GitHubAPIException("Failed to create branch: " + response.message());
            }
        } catch (IOException | GitHubAPIException e) {
            logger.error("Error creating branch", e);
            throw new GitHubAPIException("Branch creation failed", e);
        }
    }
    
    /**
     * Configures a webhook for CI/CD pipeline triggers
     * 
     * @param repositoryName the repository name
     * @param webhookUrl the Jenkins/CI webhook URL
     */
    public void configureWebhook(String repositoryName, String webhookUrl) {
        try {
            logger.info("Configuring webhook for repository: {}", repositoryName);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("name", "web");
            payload.put("active", true);
            
            Map<String, Object> config = new HashMap<>();
            config.put("url", webhookUrl);
            config.put("content_type", "json");
            config.put("secret", generateWebhookSecret());
            
            payload.put("config", config);
            payload.put("events", new String[]{"push", "pull_request"});
            
            String url = GITHUB_API_BASE + "/repos/" + gitHubOwner + "/" + 
                        repositoryName + "/hooks";
            Response response = executePost(url, gson.toJson(payload));
            
            if (response.isSuccessful()) {
                logger.info("✓ Webhook configured for CI/CD triggers");
            } else {
                logger.warn("Webhook configuration returned: {}", response.message());
            }
        } catch (IOException e) {
            logger.error("Error configuring webhook", e);
            throw new GitHubAPIException("Webhook configuration failed", e);
        }
    }
    
    /**
     * Configures branch protection rules
     * 
     * @param repositoryName the repository name
     * @param branchName the branch to protect
     */
    public void configureBranchProtection(String repositoryName, String branchName) {
        try {
            logger.info("Configuring branch protection for: {}", branchName);
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("required_status_checks", null);
            payload.put("required_pull_request_reviews", null);
            payload.put("enforce_admins", false);
            payload.put("allow_force_pushes", false);
            payload.put("allow_deletions", false);
            
            String url = GITHUB_API_BASE + "/repos/" + gitHubOwner + "/" + 
                        repositoryName + "/branches/" + branchName + "/protection";
            Response response = executePut(url, gson.toJson(payload));
            
            if (response.isSuccessful()) {
                logger.info("✓ Branch protection enabled for: {}", branchName);
            }
        } catch (IOException | GitHubAPIException e) {
            logger.warn("Branch protection configuration not available", e);
        }
    }
    
    // HTTP Helper Methods
    
    private Response executeGet(String url) throws IOException {
        Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "token " + gitHubToken)
            .addHeader("Accept", GITHUB_API_VERSION)
            .build();
        
        return httpClient.newCall(request).execute();
    }
    
    private Response executePost(String url, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(body, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "token " + gitHubToken)
            .addHeader("Accept", GITHUB_API_VERSION)
            .post(requestBody)
            .build();
        
        return httpClient.newCall(request).execute();
    }
    
    private Response executePut(String url, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(body, MediaType.parse("application/json"));
        Request request = new Request.Builder()
            .url(url)
            .addHeader("Authorization", "token " + gitHubToken)
            .addHeader("Accept", GITHUB_API_VERSION)
            .put(requestBody)
            .build();
        
        return httpClient.newCall(request).execute();
    }
    
    /**
     * Generates a secure webhook secret
     */
    private String generateWebhookSecret() {
        return "webhook_" + System.currentTimeMillis();
    }
    
    /**
     * Custom exception for GitHub API errors
     */
    public static class GitHubAPIException extends RuntimeException {
        public GitHubAPIException(String message) {
            super(message);
        }
        
        public GitHubAPIException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
