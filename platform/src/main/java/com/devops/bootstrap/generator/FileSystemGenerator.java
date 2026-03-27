package com.devops.bootstrap.generator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devops.bootstrap.platform.ServiceModuleConfiguration;

/**
 * FileSystemGenerator creates the complete microservice directory structure
 * and generates all necessary boilerplate files.
 * 
 * Directory structure created:
 * ├── src/main/java/[package]/
 * │   ├── [ServiceName]Application.java (Spring Boot entry point)
 * │   ├── controller/[ServiceName]Controller.java (REST endpoints)
 * │   ├── service/[ServiceName]Service.java (Business logic)
 * │   ├── repository/[ServiceName]Repository.java (Data access)
 * │   ├── entity/[ServiceName]Entity.java (JPA entity)
 * │   ├── dto/[ServiceName]DTO.java (API contract)
 * │   └── exception/ (Exception handlers)
 * ├── src/main/resources/
 * │   ├── application.properties
 * │   ├── application-dev.properties
 * │   ├── application-prod.properties
 * │   └── logback-spring.xml
 * ├── src/test/java/ (Test classes)
 * ├── pom.xml
 * ├── Dockerfile
 * ├── docker-compose.yml
 * ├── Jenkinsfile
 * ├── .github/workflows/
 * ├── .gitignore
 * └── README.md
 */
public class FileSystemGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(FileSystemGenerator.class);
    
    private final String baseOutputPath;
    private final TemplateEngine templateEngine;
    
    public FileSystemGenerator(String baseOutputPath, TemplateEngine templateEngine) {
        this.baseOutputPath = baseOutputPath;
        this.templateEngine = templateEngine;
    }
    
    /**
     * Generates the complete microservice project structure
     */
    public String generateProject(ServiceModuleConfiguration config) throws IOException {
        String projectPath = Paths.get(baseOutputPath, 
                                       config.getRequest().getServiceName()).toString();
        
        logger.info("Generating project structure at: {}", projectPath);
        
        // Create directory structure
        createDirectoryStructure(projectPath, config);
        
        // Generate Java source files
        generateJavaSourceFiles(projectPath, config);
        
        // Generate resources
        generateResourceFiles(projectPath, config);
        
        // Generate test files
        generateTestFiles(projectPath, config);
        
        // Generate configuration files
        generateConfigurationFiles(projectPath, config);
        
        logger.info("✓ Project generation completed at: {}", projectPath);
        return projectPath;
    }
    
    /**
     * Creates the complete directory tree
     */
    private void createDirectoryStructure(String projectPath, ServiceModuleConfiguration config) 
        throws IOException {
        String packagePath = config.getFullPackagePath();
        
        // Create all necessary directories
        Files.createDirectories(Paths.get(projectPath, "src", "main", "java", packagePath, "controller"));
        Files.createDirectories(Paths.get(projectPath, "src", "main", "java", packagePath, "service"));
        Files.createDirectories(Paths.get(projectPath, "src", "main", "java", packagePath, "repository"));
        Files.createDirectories(Paths.get(projectPath, "src", "main", "java", packagePath, "entity"));
        Files.createDirectories(Paths.get(projectPath, "src", "main", "java", packagePath, "dto"));
        Files.createDirectories(Paths.get(projectPath, "src", "main", "java", packagePath, "exception"));
        Files.createDirectories(Paths.get(projectPath, "src", "main", "java", packagePath, "config"));
        
        Files.createDirectories(Paths.get(projectPath, "src", "main", "resources"));
        
        Files.createDirectories(Paths.get(projectPath, "src", "test", "java", packagePath));
        
        Files.createDirectories(Paths.get(projectPath, ".github", "workflows"));
        
        logger.info("✓ Directory structure created");
    }
    
    /**
     * Generates Java source files
     */
    private void generateJavaSourceFiles(String projectPath, ServiceModuleConfiguration config) 
        throws IOException {
        String packagePath = config.getFullPackagePath();
        String mainSrcPath = Paths.get(projectPath, "src", "main", "java", packagePath).toString();
        
        // Spring Boot Application class
        generateFile(
            mainSrcPath,
            config.getApplicationClassName() + ".java",
            getApplicationClassTemplate(config),
            config
        );
        
        // REST Controller
        generateFile(
            Paths.get(mainSrcPath, "controller").toString(),
            config.getControllerClassName() + ".java",
            getControllerTemplate(config),
            config
        );
        
        // Service/Business Logic
        generateFile(
            Paths.get(mainSrcPath, "service").toString(),
            config.getServiceClassName() + ".java",
            getServiceTemplate(config),
            config
        );
        
        // Repository
        generateFile(
            Paths.get(mainSrcPath, "repository").toString(),
            config.getRepositoryClassName() + ".java",
            getRepositoryTemplate(config),
            config
        );
        
        // Entity
        generateFile(
            Paths.get(mainSrcPath, "entity").toString(),
            config.getEntityClassName() + ".java",
            getEntityTemplate(config),
            config
        );
        
        // DTO
        generateFile(
            Paths.get(mainSrcPath, "dto").toString(),
            config.getDtoClassName() + ".java",
            getDtoTemplate(config),
            config
        );
        
        // Global Exception Handler
        generateFile(
            Paths.get(mainSrcPath, "exception").toString(),
            "GlobalExceptionHandler.java",
            getGlobalExceptionHandlerTemplate(config),
            config
        );
        
        // Application Configuration
        generateFile(
            Paths.get(mainSrcPath, "config").toString(),
            "ApplicationConfiguration.java",
            getApplicationConfigurationTemplate(config),
            config
        );
        
        logger.info("✓ Java source files generated");
    }
    
    /**
     * Generates resource files (properties, XML configs)
     */
    private void generateResourceFiles(String projectPath, ServiceModuleConfiguration config) 
        throws IOException {
        String resourcePath = Paths.get(projectPath, "src", "main", "resources").toString();
        
        // application.properties
        generateFile(
            resourcePath,
            "application.properties",
            getApplicationPropertiesTemplate(config),
            config
        );
        
        // application-dev.properties
        generateFile(
            resourcePath,
            "application-dev.properties",
            getApplicationDevPropertiesTemplate(config),
            config
        );
        
        // application-prod.properties
        generateFile(
            resourcePath,
            "application-prod.properties",
            getApplicationProdPropertiesTemplate(config),
            config
        );
        
        // logback-spring.xml
        generateFile(
            resourcePath,
            "logback-spring.xml",
            getLogbackTemplate(config),
            config
        );
        
        logger.info("✓ Resource files generated");
    }
    
    /**
     * Generates test files
     */
    private void generateTestFiles(String projectPath, ServiceModuleConfiguration config) 
        throws IOException {
        String testPath = Paths.get(projectPath, "src", "test", "java", config.getFullPackagePath()).toString();
        
        // Controller Test
        generateFile(
            testPath,
            config.getControllerClassName() + "Test.java",
            getControllerTestTemplate(config),
            config
        );
        
        // Service Test
        generateFile(
            testPath,
            config.getServiceClassName() + "Test.java",
            getServiceTestTemplate(config),
            config
        );
        
        logger.info("✓ Test files generated");
    }
    
    /**
     * Generates configuration files (Docker, Jenkins, pom.xml, etc.)
     */
    private void generateConfigurationFiles(String projectPath, ServiceModuleConfiguration config) 
        throws IOException {
        
        // pom.xml
        generateFile(
            projectPath,
            "pom.xml",
            getPomTemplate(config),
            config
        );
        
        // Jenkinsfile
        generateFile(
            projectPath,
            "Jenkinsfile",
            new JenkinsPipelineGenerator(templateEngine).generateJenkinsfileTemplate(config),
            config
        );
        
        // Dockerfile
        generateFile(
            projectPath,
            "Dockerfile",
            new DockerGenerator(templateEngine).getDockerfileTemplate(config),
            config
        );
        
        // docker-compose.yml
        generateFile(
            projectPath,
            "docker-compose.yml",
            new DockerGenerator(templateEngine).getDockerComposeTemplate(config),
            config
        );
        generateFile(
            projectPath,
            "pom.xml",
            getPomTemplate(config),
            config
        );

        // .gitignore
        generateFile(
            projectPath,
            ".gitignore",
            getGitignoreTemplate(),
            config
        );
        
        // README.md
        generateFile(
            projectPath,
            "README.md",
            getReadmeTemplate(config),
            config
        );
        
        logger.info("✓ Configuration files generated");
    }
    
    /**
     * Helper method to generate a file from template
     */
    private void generateFile(String dirPath, String fileName, String templateContent,
                            ServiceModuleConfiguration config) throws IOException {
        String filePath = Paths.get(dirPath, fileName).toString();
        templateEngine.generateFile(templateContent, filePath, config);
    }
    
    // Template Methods
    
    private String getApplicationClassTemplate(ServiceModuleConfiguration config) {
        return "package ${package};\n\n" +
               "import org.springframework.boot.SpringApplication;\n" +
               "import org.springframework.boot.autoconfigure.SpringBootApplication;\n" +
               "import org.springframework.context.annotation.ComponentScan;\n" +
               "import org.slf4j.Logger;\n" +
               "import org.slf4j.LoggerFactory;\n\n" +
               "@SpringBootApplication\n" +
               "@ComponentScan(basePackages = {\"${package}\"})\n" +
               "public class ${applicationClass} {\n\n" +
               "    private static final Logger logger = LoggerFactory.getLogger(${applicationClass}.class);\n\n" +
               "    public static void main(String[] args) {\n" +
               "        SpringApplication.run(${applicationClass}.class, args);\n" +
               "        logger.info(\"✓ ${serviceName_pascal} started successfully on port ${port}\");\n" +
               "    }\n" +
               "}\n";
    }
    
    private String getControllerTemplate(ServiceModuleConfiguration config) {
        return "package ${package}.controller;\n\n" +
               "import ${package}.dto.${dtoClass};\n" +
               "import ${package}.service.${serviceClass};\n" +
               "import ${package}.entity.${entityClass};\n" +
               "import org.slf4j.Logger;\n" +
               "import org.slf4j.LoggerFactory;\n" +
               "import org.springframework.beans.factory.annotation.Autowired;\n" +
               "import org.springframework.http.HttpStatus;\n" +
               "import org.springframework.http.ResponseEntity;\n" +
               "import org.springframework.web.bind.annotation.*;\n\n" +
               "@RestController\n" +
               "@RequestMapping(\"/${serviceName}\")\n" +
               "@CrossOrigin(origins = \"*\")\n" +
               "public class ${controllerClass} {\n\n" +
               "    private static final Logger logger = LoggerFactory.getLogger(${controllerClass}.class);\n\n" +
               "    @Autowired\n" +
               "    private ${serviceClass} ${serviceName_camel}Service;\n\n" +
               "    @GetMapping(\"/health\")\n" +
               "    public ResponseEntity<String> health() {\n" +
               "        logger.debug(\"Health check endpoint called\");\n" +
               "        return ResponseEntity.ok(\"${serviceName_pascal} is healthy\");\n" +
               "    }\n\n" +
               "    @GetMapping\n" +
               "    public ResponseEntity<?> getAll() {\n" +
               "        logger.info(\"Fetching all records\");\n" +
               "        return ResponseEntity.ok(${serviceName_camel}Service.getAll());\n" +
               "    }\n\n" +
               "    @GetMapping(\"/{id}\")\n" +
               "    public ResponseEntity<?> getById(@PathVariable Long id) {\n" +
               "        logger.info(\"Fetching record with id: {}\", id);\n" +
               "        return ResponseEntity.ok(${serviceName_camel}Service.getById(id));\n" +
               "    }\n\n" +
               "    @PostMapping\n" +
               "    public ResponseEntity<?> create(@RequestBody ${dtoClass} dto) {\n" +
               "        logger.info(\"Creating new record\");\n" +
               "        return ResponseEntity.status(HttpStatus.CREATED)\n" +
               "            .body(${serviceName_camel}Service.create(dto));\n" +
               "    }\n\n" +
               "    @PutMapping(\"/{id}\")\n" +
               "    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ${dtoClass} dto) {\n" +
               "        logger.info(\"Updating record with id: {}\", id);\n" +
               "        return ResponseEntity.ok(${serviceName_camel}Service.update(id, dto));\n" +
               "    }\n\n" +
               "    @DeleteMapping(\"/{id}\")\n" +
               "    public ResponseEntity<?> delete(@PathVariable Long id) {\n" +
               "        logger.info(\"Deleting record with id: {}\", id);\n" +
               "        ${serviceName_camel}Service.delete(id);\n" +
               "        return ResponseEntity.noContent().build();\n" +
               "    }\n" +
               "}\n";
    }
    
    private String getServiceTemplate(ServiceModuleConfiguration config) {
        return "package ${package}.service;\n\n" +
               "import ${package}.dto.${dtoClass};\n" +
               "import ${package}.entity.${entityClass};\n" +
               "import ${package}.repository.${repositoryClass};\n" +
               "import org.slf4j.Logger;\n" +
               "import org.slf4j.LoggerFactory;\n" +
               "import org.springframework.beans.factory.annotation.Autowired;\n" +
               "import org.springframework.stereotype.Service;\n" +
               "import org.springframework.transaction.annotation.Transactional;\n" +
               "import java.util.List;\n" +
               "import java.util.stream.Collectors;\n\n" +
               "@Service\n" +
               "@Transactional\n" +
               "public class ${serviceClass} {\n\n" +
               "    private static final Logger logger = LoggerFactory.getLogger(${serviceClass}.class);\n\n" +
               "    @Autowired\n" +
               "    private ${repositoryClass} repository;\n\n" +
               "    public List<${dtoClass}> getAll() {\n" +
               "        logger.debug(\"Fetching all records\");\n" +
               "        return repository.findAll()\n" +
               "            .stream()\n" +
               "            .map(this::convertToDTO)\n" +
               "            .collect(Collectors.toList());\n" +
               "    }\n\n" +
               "    public ${dtoClass} getById(Long id) {\n" +
               "        logger.debug(\"Fetching record with id: {}\", id);\n" +
               "        return repository.findById(id)\n" +
               "            .map(this::convertToDTO)\n" +
               "            .orElse(null);\n" +
               "    }\n\n" +
               "    public ${dtoClass} create(${dtoClass} dto) {\n" +
               "        logger.info(\"Creating new record\");\n" +
               "        ${entityClass} entity = convertToEntity(dto);\n" +
               "        return convertToDTO(repository.save(entity));\n" +
               "    }\n\n" +
               "    public ${dtoClass} update(Long id, ${dtoClass} dto) {\n" +
               "        logger.info(\"Updating record with id: {}\", id);\n" +
               "        var entity = repository.findById(id).orElse(null);\n" +
               "        if (entity != null) {\n" +
               "            repository.save(entity);\n" +
               "        }\n" +
               "        return convertToDTO(entity);\n" +
               "    }\n\n" +
               "    public void delete(Long id) {\n" +
               "        logger.info(\"Deleting record with id: {}\", id);\n" +
               "        repository.deleteById(id);\n" +
               "    }\n\n" +
               "    private ${dtoClass} convertToDTO(${entityClass} entity) {\n" +
               "        ${dtoClass} dto = new ${dtoClass}();\n" +
               "        return dto;\n" +
               "    }\n\n" +
               "    private ${entityClass} convertToEntity(${dtoClass} dto) {\n" +
               "        ${entityClass} entity = new ${entityClass}();\n" +
               "        return entity;\n" +
               "    }\n" +
               "}\n";
    }
    
    private String getRepositoryTemplate(ServiceModuleConfiguration config) {
        return "package ${package}.repository;\n\n" +
               "import ${package}.entity.${entityClass};\n" +
               "import org.springframework.data.jpa.repository.JpaRepository;\n" +
               "import org.springframework.stereotype.Repository;\n\n" +
               "@Repository\n" +
               "public interface ${repositoryClass} extends JpaRepository<${entityClass}, Long> {\n" +
               "    // Custom query methods can be added here\n" +
               "}\n";
    }
    
    private String getEntityTemplate(ServiceModuleConfiguration config) {
        return "package ${package}.entity;\n\n" +
               "import jakarta.persistence.*;\n" +
               "import java.io.Serializable;\n" +
               "import java.time.LocalDateTime;\n\n" +
               "@Entity\n" +
               "@Table(name = \"${serviceName}\")\n" +
               "public class ${entityClass} implements Serializable {\n\n" +
               "    private static final long serialVersionUID = 1L;\n\n" +
               "    @Id\n" +
               "    @GeneratedValue(strategy = GenerationType.IDENTITY)\n" +
               "    private Long id;\n\n" +
               "    @Column(nullable = false, length = 100)\n" +
               "    private String name;\n\n" +
               "    @Column(columnDefinition = \"TEXT\")\n" +
               "    private String description;\n\n" +
               "    @Column(nullable = false, updatable = false)\n" +
               "    private LocalDateTime createdAt = LocalDateTime.now();\n\n" +
               "    @Column(nullable = false)\n" +
               "    private LocalDateTime updatedAt = LocalDateTime.now();\n\n" +
               "    // Constructors\n" +
               "    public ${entityClass}() {}\n\n" +
               "    public ${entityClass}(String name, String description) {\n" +
               "        this.name = name;\n" +
               "        this.description = description;\n" +
               "    }\n\n" +
               "    // Getters and Setters\n" +
               "    public Long getId() { return id; }\n" +
               "    public void setId(Long id) { this.id = id; }\n\n" +
               "    public String getName() { return name; }\n" +
               "    public void setName(String name) { this.name = name; }\n\n" +
               "    public String getDescription() { return description; }\n" +
               "    public void setDescription(String description) { this.description = description; }\n\n" +
               "    public LocalDateTime getCreatedAt() { return createdAt; }\n\n" +
               "    public LocalDateTime getUpdatedAt() { return updatedAt; }\n" +
               "    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }\n" +
               "}\n";
    }
    
    private String getDtoTemplate(ServiceModuleConfiguration config) {
        return "package ${package}.dto;\n\n" +
               "import java.io.Serializable;\n\n" +
               "public class ${dtoClass} implements Serializable {\n\n" +
               "    private static final long serialVersionUID = 1L;\n\n" +
               "    private Long id;\n" +
               "    private String name;\n" +
               "    private String description;\n\n" +
               "    // Constructors\n" +
               "    public ${dtoClass}() {}\n\n" +
               "    public ${dtoClass}(String name, String description) {\n" +
               "        this.name = name;\n" +
               "        this.description = description;\n" +
               "    }\n\n" +
               "    // Getters and Setters\n" +
               "    public Long getId() { return id; }\n" +
               "    public void setId(Long id) { this.id = id; }\n\n" +
               "    public String getName() { return name; }\n" +
               "    public void setName(String name) { this.name = name; }\n\n" +
               "    public String getDescription() { return description; }\n" +
               "    public void setDescription(String description) { this.description = description; }\n" +
               "}\n";
    }
    
    private String getGlobalExceptionHandlerTemplate(ServiceModuleConfiguration config) {
        return "package ${package}.exception;\n\n" +
               "import org.slf4j.Logger;\n" +
               "import org.slf4j.LoggerFactory;\n" +
               "import org.springframework.http.HttpStatus;\n" +
               "import org.springframework.http.ResponseEntity;\n" +
               "import org.springframework.web.bind.annotation.ExceptionHandler;\n" +
               "import org.springframework.web.bind.annotation.RestControllerAdvice;\n" +
               "import java.util.HashMap;\n" +
               "import java.util.Map;\n\n" +
               "@RestControllerAdvice\n" +
               "public class GlobalExceptionHandler {\n\n" +
               "    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);\n\n" +
               "    @ExceptionHandler(Exception.class)\n" +
               "    public ResponseEntity<Map<String, String>> handleException(Exception e) {\n" +
               "        logger.error(\"Unexpected error occurred\", e);\n" +
               "        Map<String, String> error = new HashMap<>();\n" +
               "        error.put(\"error\", \"Internal Server Error\");\n" +
               "        error.put(\"message\", e.getMessage());\n" +
               "        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);\n" +
               "    }\n" +
               "}\n";
    }
    
    private String getApplicationConfigurationTemplate(ServiceModuleConfiguration config) {
        return "package ${package}.config;\n\n" +
               "import org.springframework.context.annotation.Configuration;\n" +
               "import org.springframework.context.annotation.EnableAspectJAutoProxy;\n\n" +
               "@Configuration\n" +
               "@EnableAspectJAutoProxy(proxyTargetClass = true)\n" +
               "public class ApplicationConfiguration {\n" +
               "    // Application configuration beans can be defined here\n" +
               "}\n";
    }
    
    private String getApplicationPropertiesTemplate(ServiceModuleConfiguration config) {
        return "spring.application.name=${serviceName}\n" +
               "server.port=${port}\n" +
               "server.servlet.context-path=/api/v1\n\n" +
               "# JPA/Hibernate Configuration\n" +
               "spring.jpa.hibernate.ddl-auto=validate\n" +
               "spring.jpa.show-sql=false\n" +
               "spring.jpa.properties.hibernate.format_sql=true\n" +
               "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect\n\n" +
               "# Logging\n" +
               "logging.level.root=INFO\n" +
               "logging.level.${package}=DEBUG\n" +
               "logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n\n";
    }
    
    private String getApplicationDevPropertiesTemplate(ServiceModuleConfiguration config) {
        return "server.port=${port}\n\n" +
               "# Database Configuration (Development)\n" +
               "spring.datasource.url=jdbc:postgresql://${d}{DB_HOST:localhost}:5432/${serviceName}\n" +
               "spring.datasource.username=${d}{DB_USER:postgres}\n" +
               "spring.datasource.password=${d}{DB_PASSWORD:password}\n" +
               "spring.jpa.hibernate.ddl-auto=create-drop\n\n" +
               "# Logging (Development)\n" +
               "logging.level.root=INFO\n" +
               "logging.level.${package}=DEBUG\n" +
               "logging.level.org.springframework.web=DEBUG\n";
    }
    
    private String getApplicationProdPropertiesTemplate(ServiceModuleConfiguration config) {
        // Use ${d} (injected into context as "$") to safely write Spring Boot property placeholders
        return "server.port=${port}\n" +
               "server.shutdown=graceful\n\n" +
               "# Database Configuration (Production)\n" +
               "spring.datasource.url=jdbc:postgresql://${d}{DB_HOST:localhost}:5432/${serviceName}\n" +
               "spring.datasource.username=${d}{DB_USER:postgres}\n" +
               "spring.datasource.password=${d}{DB_PASSWORD:password}\n" +
               "spring.jpa.hibernate.ddl-auto=validate\n\n" +
               "# Logging (Production)\n" +
               "logging.level.root=WARN\n" +
               "logging.level.${package}=INFO\n";
    }
    
    private String getLogbackTemplate(ServiceModuleConfiguration config) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<configuration>\n" +
               "    <include resource=\"org/springframework/boot/logging/logback/defaults.xml\" />\n" +
               "    <include resource=\"org/springframework/boot/logging/logback/console-appender.xml\" />\n\n" +
               "    <springProperty scope=\"context\" name=\"LOG_FILE\" source=\"logging.file.name\" defaultValue=\"logs/${serviceName}.log\"/>\n\n" +
               "    <appender name=\"FILE\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">\n" +
               "        <file>${d}{LOG_FILE}</file>\n" +
               "        <encoder>\n" +
               "            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>\n" +
               "        </encoder>\n" +
               "        <rollingPolicy class=\"ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy\">\n" +
               "            <fileNamePattern>logs/${serviceName}-%d{yyyy-MM-dd}.%i.log</fileNamePattern>\n" +
               "            <maxFileSize>100MB</maxFileSize>\n" +
               "            <maxHistory>30</maxHistory>\n" +
               "        </rollingPolicy>\n" +
               "    </appender>\n\n" +
               "    <root level=\"INFO\">\n" +
               "        <appender-ref ref=\"CONSOLE\" />\n" +
               "        <appender-ref ref=\"FILE\" />\n" +
               "    </root>\n" +
               "</configuration>\n";
    }
    
    private String getControllerTestTemplate(ServiceModuleConfiguration config) {
        return "package ${package};\n\n" +
               "import ${package}.controller.${controllerClass};\n" +
               "import ${package}.service.${serviceClass};\n" +
               "import org.junit.jupiter.api.BeforeEach;\n" +
               "import org.junit.jupiter.api.Test;\n" +
               "import org.mockito.InjectMocks;\n" +
               "import org.mockito.Mock;\n" +
               "import org.mockito.MockitoAnnotations;\n" +
               "import org.springframework.http.HttpStatus;\n" +
               "import org.springframework.http.ResponseEntity;\n\n" +
               "import static org.junit.jupiter.api.Assertions.assertEquals;\n\n" +
               "class ${controllerClass}Test {\n\n" +
               "    @Mock\n" +
               "    private ${serviceClass} ${serviceName_camel}Service;\n\n" +
               "    @InjectMocks\n" +
               "    private ${controllerClass} controller;\n\n" +
               "    @BeforeEach\n" +
               "    void setUp() {\n" +
               "        MockitoAnnotations.openMocks(this);\n" +
               "    }\n\n" +
               "    @Test\n" +
               "    void testHealth() {\n" +
               "        ResponseEntity<String> response = controller.health();\n" +
               "        assertEquals(HttpStatus.OK, response.getStatusCode());\n" +
               "    }\n" +
               "}\n";
    }
    
    private String getServiceTestTemplate(ServiceModuleConfiguration config) {
        return "package ${package};\n\n" +
               "import ${package}.service.${serviceClass};\n" +
               "import org.junit.jupiter.api.BeforeEach;\n" +
               "import org.junit.jupiter.api.Test;\n\n" +
               "import static org.junit.jupiter.api.Assertions.assertNotNull;\n\n" +
               "class ${serviceClass}Test {\n\n" +
               "    private ${serviceClass} service;\n\n" +
               "    @BeforeEach\n" +
               "    void setUp() {\n" +
               "        service = new ${serviceClass}();\n" +
               "    }\n\n" +
               "    @Test\n" +
               "    void testServiceInitialization() {\n" +
               "        assertNotNull(service);\n" +
               "    }\n" +
               "}\n";
    }
    
    private String getPomTemplate(ServiceModuleConfiguration config) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
               "    xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
               "    <modelVersion>4.0.0</modelVersion>\n" +
               "    <parent>\n" +
               "        <groupId>org.springframework.boot</groupId>\n" +
               "        <artifactId>spring-boot-starter-parent</artifactId>\n" +
               "        <version>3.2.0</version>\n" +
               "        <relativePath/> <!-- lookup parent from repository -->\n" +
               "    </parent>\n" +
               "    <groupId>${mavenGroupId}</groupId>\n" +
               "    <artifactId>${mavenArtifactId}</artifactId>\n" +
               "    <version>${mavenVersion}</version>\n" +
               "    <name>${serviceName}</name>\n" +
               "    <description>${description}</description>\n" +
               "    <properties>\n" +
               "        <java.version>17</java.version>\n" +
               "        <jacoco.version>0.8.11</jacoco.version>\n" +
               "    </properties>\n" +
               "    <dependencies>\n" +
               "        <dependency>\n" +
               "            <groupId>org.springframework.boot</groupId>\n" +
               "            <artifactId>spring-boot-starter-web</artifactId>\n" +
               "        </dependency>\n" +
               "        <dependency>\n" +
               "            <groupId>org.springframework.boot</groupId>\n" +
               "            <artifactId>spring-boot-starter-data-jpa</artifactId>\n" +
               "        </dependency>\n" +
               "        <dependency>\n" +
               "            <groupId>org.postgresql</groupId>\n" +
               "            <artifactId>postgresql</artifactId>\n" +
               "            <scope>runtime</scope>\n" +
               "        </dependency>\n" +
               "        <dependency>\n" +
               "            <groupId>org.springframework.boot</groupId>\n" +
               "            <artifactId>spring-boot-starter-test</artifactId>\n" +
               "            <scope>test</scope>\n" +
               "        </dependency>\n" +
               "    </dependencies>\n" +
               "    <build>\n" +
               "        <plugins>\n" +
               "            <plugin>\n" +
               "                <groupId>org.springframework.boot</groupId>\n" +
               "                <artifactId>spring-boot-maven-plugin</artifactId>\n" +
               "            </plugin>\n" +
               "            <plugin>\n" +
               "                <groupId>org.jacoco</groupId>\n" +
               "                <artifactId>jacoco-maven-plugin</artifactId>\n" +
               "                <version>${d}{jacoco.version}</version>\n" +
               "                <executions>\n" +
               "                    <execution>\n" +
               "                        <id>default-prepare-agent</id>\n" +
               "                        <goals>\n" +
               "                            <goal>prepare-agent</goal>\n" +
               "                        </goals>\n" +
               "                    </execution>\n" +
               "                    <execution>\n" +
               "                        <id>default-report</id>\n" +
               "                        <phase>test</phase>\n" +
               "                        <goals>\n" +
               "                            <goal>report</goal>\n" +
               "                        </goals>\n" +
               "                    </execution>\n" +
               "                </executions>\n" +
               "            </plugin>\n" +
               "        </plugins>\n" +
               "    </build>\n" +
               "</project>\n";
    }

    private String getGitignoreTemplate() {
        return "# Java\n" +
               "*.class\n" +
               "*.jar\n" +
               "*.war\n" +
               "target/\n" +
               "*.log\n\n" +
               "# Maven\n" +
               ".mvn/\n" +
               "maven-wrapper.jar\n" +
               "maven-wrapper.properties\n\n" +
               "# IDE\n" +
               ".idea/\n" +
               ".vscode/\n" +
               "*.swp\n" +
               "*.swo\n" +
               "*~\n" +
               ".DS_Store\n\n" +
               "# Build\n" +
               "build/\n" +
               "dist/\n\n" +
               "# Docker\n" +
               ".docker/\n\n" +
               "# Environment\n" +
               ".env\n" +
               ".env.local\n";
    }
    
    private String getReadmeTemplate(ServiceModuleConfiguration config) {
        return "# ${serviceName_pascal}\n\n" +
               config.getRequest().getDescription() + "\n\n" +
               "## Quick Start\n\n" +
               "### Prerequisites\n" +
               "- Java 17+\n" +
               "- Maven 3.9+\n" +
               "- Docker (optional)\n\n" +
               "### Installation\n\n" +
               "```bash\n" +
               "mvn clean install\n" +
               "mvn spring-boot:run\n" +
               "```\n\n" +
               "### API Endpoints\n\n" +
               "- `GET /api/v1/${serviceName}/health` - Service health check\n" +
               "- `GET /api/v1/${serviceName}` - List all records\n" +
               "- `POST /api/v1/${serviceName}` - Create new record\n\n" +
               "### Docker\n\n" +
               "```bash\n" +
               "docker-compose up -d\n" +
               "```\n\n" +
               "### Code Quality\n\n" +
               "Generate JaCoCo coverage report:\n" +
               "```bash\n" +
               "mvn clean test jacoco:report\n" +
               "```\n\n" +
               "Generated by Microservice Bootstrap Automation Platform\n";
    }
}
