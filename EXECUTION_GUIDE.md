# COMPREHENSIVE SETUP & EXECUTION GUIDE
# Microservice Bootstrap Automation Platform v1.0.0

## [EXECUTION GUIDE]

### Prerequisites
```
✓ Java 17+ (OpenJDK or Oracle JDK)
✓ Maven 3.9.0+
✓ Git 2.40+
✓ Docker 24.0+ (optional, for containerization)
✓ GitHub Account with Personal Access Token (optional, for repo creation)
✓ Jenkins 2.414+ (optional, for CI/CD)
```

### Step 1: Clone the Platform Repository
```bash
git clone https://github.com/company/bootstrap-platform.git
cd bootstrap-platform
```

### Step 2: Build the Platform
```bash
# Clean, build, and install dependencies
mvn clean install

# Expected output:
# [INFO] BUILD SUCCESS
# [INFO] Total time: XX.XXX s
```

### Step 3: Run JaCoCo Code Quality Analysis
```bash
# Generate coverage report
mvn clean test jacoco:report

# View report:
# - Open: target/site/jacoco/index.html in browser
# - Check: Coverage metrics for all modules
# NOTE: Coverage reports are generated for visibility, not enforced as hard gates
```

### Step 4: Run the Bootstrap Platform
```bash
# Option A: Direct Java execution
mvn spring-boot:run

# Option B: Run as JAR
mvn package
java -jar target/bootstrap-platform-1.0.0.jar

# Expected output:
# ✓ Microservice Bootstrap Platform started successfully
# Server running on: http://localhost:8080/platform
```

### Step 5: Test Bootstrap Functionality

#### Using cURL (REST API Test)
```bash
# Generate a microservice
curl -X POST http://localhost:8080/platform/api/bootstrap \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "product-service",
    "port": "8082",
    "packageName": "com.company",
    "databaseType": "PostgreSQL",
    "gitHubToken": "ghp_your_token_here",
    "gitHubOwner": "your-org",
    "description": "Product Management Microservice"
  }'

# Expected response:
{
  "serviceName": "product-service",
  "port": "8082",
  "package": "com.company.productservice",
  "projectPath": "/generated-microservices/product-service",
  "repositoryUrl": "https://github.com/your-org/product-service.git",
  "dockerImage": "com-company/product-service",
  "status": "SUCCESS"
}
```

#### Using Docker
```bash
# Build Docker image
docker build -t bootstrap-platform:1.0 .

# Run with Docker Compose
docker-compose up -d

# Access API:
# http://localhost:8080/platform/api/health
# http://localhost:8080/platform/api/bootstrap (POST)
```

---

## [TESTING & VALIDATION]

### Unit Tests
```bash
# Run all tests with coverage
mvn clean test

# Run specific test class
mvn test -Dtest=ValidationEngineTest

# Expected output:
# [INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: 0
```

### Integration Tests
```bash
# Test complete bootstrap workflow
mvn verify

# Test with all integration scenarios
mvn clean verify -Dgroups=integration
```

### Code Quality Validation

#### JaCoCo Coverage Report
```bash
# Generate detailed coverage report
mvn clean test jacoco:report

# View in browser:
# file:///path/to/target/site/jacoco/index.html

# Check specific coverage:
# - Line Coverage: >= 80% (enforced by pom.xml)
# - Branch Coverage: Reported
# - Method Coverage: Reported
```

#### Static Code Analysis (Optional SonarQube)
```bash
# If SonarQube is configured:
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=bootstrap-platform \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=your_sonarqube_token
```

### Functional Validation

#### Test 1: Validate Input Validation
```bash
curl -X POST http://localhost:8080/platform/api/bootstrap \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "invalid!service",
    "port": "99999",
    "packageName": "InvalidPackage",
    "databaseType": "InvalidDB",
    "gitHubToken": "",
    "gitHubOwner": ""
  }'

# Expected: 400 Bad Request with validation errors
```

#### Test 2: Validate Boilerplate Generation
```bash
# After successful bootstrap, verify generated files:
ls -la /generated-microservices/product-service/

# Expected structure:
# ├── src/main/java/com/company/productservice/
# │   ├── ProductServiceApplication.java
# │   ├── controller/ProductServiceController.java
# │   ├── service/ProductServiceService.java
# │   ├── entity/ProductServiceEntity.java
# │   ├── dto/ProductServiceDTO.java
# │   └── repository/ProductServiceRepository.java
# ├── src/test/java/
# ├── src/main/resources/
# ├── pom.xml
# ├── Dockerfile
# ├── docker-compose.yml
# ├── Jenkinsfile
# └── README.md
```

#### Test 3: Verify Docker Build
```bash
cd /generated-microservices/product-service/

# Build Docker image
docker build -t product-service:1.0 .

# Verify image
docker images | grep product-service

# Expected: Image successfully created with reasonable size
```

#### Test 4: Verify Jenkins Pipeline
```bash
# Check Jenkinsfile syntax
cat /generated-microservices/product-service/Jenkinsfile

# Validate with Jenkins (if available):
curl -X POST http://jenkins-server/pipeline-model-converter/validate \
  -F jenkinsfile=@Jenkinsfile

# Expected: Pipeline is valid
```

#### Test 5: Verify JaCoCo Configuration
```bash
cd /generated-microservices/product-service/

# Generate coverage report
mvn clean test jacoco:report

# View report
cat target/site/jacoco/index.html | grep "Coverage"

# Expected: >= 80% coverage  
```

### Performance Validation

#### Bootstrap Generation Time
```bash
# Measure complete generation time
time curl -X POST http://localhost:8080/platform/api/bootstrap \
  -H "Content-Type: application/json" \
  -d @request.json

# Expected: < 30 seconds for complete generation
```

#### Memory Usage
```bash
# Monitor memory during bootstrap
# In separate terminal:
watch -n 1 'ps aux | grep java | grep bootstrap'

# Expected: < 512MB heap usage
```

---

## [LOCAL DEVELOPMENT WORKFLOW]

### Development Setup
```bash
# 1. Clone platform
git clone https://github.com/company/bootstrap-platform.git
cd bootstrap-platform

# 2. Create IDE configuration (VSCode)
code .

# 3. Build project
mvn clean install

# 4. Start platform
mvn spring-boot:run

# 5. In parallel, start PostgreSQL (optional)
docker run --name postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:15-alpine

# 6. Test platform
curl http://localhost:8080/platform/api/health
```

### IDE Configuration (IntelliJ IDEA)
```
1. Open project in IntelliJ
2. Configure Java SDK: Project Structure → SDK → Java 17
3. Enable Annotation Processing: Settings → Build → Annotation Processors → Enable
4. Configure Maven: Settings → Build, Execution, Deployment → Maven
5. Run configuration: Run → Edit Configurations
   - Main class: com.devops.bootstrap.BootstrapPlatformApplication
   - VM options: -Xmx512m
```

### Debugging
```bash
# Run in debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

# Connect IDE debugger to localhost:5005
# Set breakpoints and step through code
```

---

## [DEPLOYMENT GUIDE]

### Docker Deployment
```bash
# Build image
docker build -t bootstrap-platform:1.0 .

# Run container
docker run -p 8080:8080 \
  -e BOOTSTRAP_OUTPUT_PATH=/generated-microservices \
  -v /generated-microservices:/generated-microservices \
  bootstrap-platform:1.0

# Verify
curl http://localhost:8080/platform/api/health
```

### Docker Compose Deployment
```bash
# Start entire infrastructure
docker-compose up -d

# View logs
docker-compose logs -f bootstrap-platform

# Stop
docker-compose down
```

### Kubernetes Deployment
```bash
# Create namespace
kubectl create namespace bootstrap

# Create ConfigMap
kubectl create configmap bootstrap-config \
  --from-literal=output.path=/generated-microservices \
  -n bootstrap

# Deploy
kubectl apply -f k8s/deployment.yaml -n bootstrap

# Verify
kubectl get pods -n bootstrap
kubectl logs -f deployment/bootstrap-platform -n bootstrap
```

---

## [TROUBLESHOOTING]

### Issue: Maven Build Fails
```bash
Solution:
# Clear Maven cache
mvn clean
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U
```

### Issue: Docker Image Build Fails
```bash
Solution:
# Check Docker daemon
docker ps

# Verify Dockerfile
docker build --no-cache -t image:tag .

# Check image
docker images | grep image
```

### Issue: GitHub Integration Fails
```bash
Solution:
# Verify token
echo $GITHUB_TOKEN

# Check token scope (must have: repo, admin:repo_hook)
curl -H "Authorization: token ${GITHUB_TOKEN}" https://api.github.com/user

# Verify owner exists
curl https://api.github.com/orgs/{owner}
```

### Issue: JaCoCo Coverage < 80%
```bash
Solution:
# Check coverage report
mvn clean test jacoco:report
open target/site/jacoco/index.html

# Identify untested code
# Write additional unit tests
# Re-run: mvn test
```

---

## [MONITORING & LOGGING]

### Application Logs
```bash
# Real-time logs
tail -f logs/bootstrap-platform.log

# Filter by level
grep ERROR logs/bootstrap-platform.log

# Filter by module
grep "com.devops.bootstrap" logs/bootstrap-platform.log
```

### Health Check
```bash
# API Health
curl -s http://localhost:8080/platform/api/health | jq

# Platform Info
curl -s http://localhost:8080/platform/api/info | jq
```

### Metrics (if Spring Boot Actuator enabled)
```bash
# Metrics endpoint
curl http://localhost:8080/actuator/metrics

# Specific metric
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

---

## [MAINTENANCE]

### Update Dependencies
```bash
# Check for updates
mvn versions:display-dependency-updates

# Update dependencies
mvn versions:use-latest-releases
```

### Backup Generated Services
```bash
# Archive all generated microservices
tar -czf microservices-backup-$(date +%Y%m%d).tar.gz /generated-microservices

# Restore from backup
tar -xzf microservices-backup-20240101.tar.gz -C /
```

### Clean Up
```bash
# Remove old generated services older than 30 days
find /generated-microservices -type d -mtime +30 -exec rm -rf {} \;

# Clear Maven cache
mvn dependency:purge-local-repository
```

---

End of Execution Guide
Generated by Microservice Bootstrap Automation Platform v1.0.0
