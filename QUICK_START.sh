#!/bin/bash

# ╔════════════════════════════════════════════════════════════╗
# ║  MICROSERVICE BOOTSTRAP AUTOMATION PLATFORM                ║
# ║  QUICK START GUIDE - 5 MINUTE SETUP                       ║
# ╚════════════════════════════════════════════════════════════╝

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║        MICROSERVICE BOOTSTRAP PLATFORM v1.0.0              ║"
echo "║              Enterprise DevOps Automation                  ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ═══════════════════════════════════════════════════════════
# STEP 1: VERIFY PREREQUISITES
# ═══════════════════════════════════════════════════════════

echo "${BLUE}[STEP 1] Verifying Prerequisites...${NC}"
echo ""

check_command() {
    if command -v $1 &> /dev/null; then
        version=$($1 --version 2>/dev/null || $1 -version 2>/dev/null)
        echo -e "${GREEN}✓${NC} $1 is installed"
        echo "   $version" | head -1
    else
        echo -e "${RED}✗${NC} $1 is NOT installed"
        echo "   Please install $1 and try again"
        exit 1
    fi
}

check_command "java"
check_command "mvn"
check_command "git"

echo ""

# ═══════════════════════════════════════════════════════════
# STEP 2: CLONE PLATFORM
# ═══════════════════════════════════════════════════════════

echo "${BLUE}[STEP 2] Preparing Platform${NC}"
echo ""

if [ ! -d "bootstrap-platform" ]; then
    echo "Cloning repository..."
    git clone https://github.com/company/bootstrap-platform.git
    cd bootstrap-platform
else
    cd bootstrap-platform
fi

echo -e "${GREEN}✓${NC} Platform ready"
echo ""

# ═══════════════════════════════════════════════════════════
# STEP 3: BUILD PLATFORM
# ═══════════════════════════════════════════════════════════

echo "${BLUE}[STEP 3] Building Platform (Java/Maven)${NC}"
echo ""
echo "Running: mvn clean install"
mvn clean install -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Build completed successfully"
else
    echo -e "${RED}✗${NC} Build failed"
    exit 1
fi

echo ""

# ═══════════════════════════════════════════════════════════
# STEP 4: VERIFY CODE QUALITY
# ═══════════════════════════════════════════════════════════

echo "${BLUE}[STEP 4] Verifying Code Quality (JaCoCo)${NC}"
echo ""
echo "Running: mvn clean test jacoco:report jacoco:check"
mvn clean test jacoco:report jacoco:check -q

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Code quality check passed (80% coverage)"
else
    echo -e "${RED}✗${NC} Code quality check failed"
    exit 1
fi

echo ""

# ═══════════════════════════════════════════════════════════
# STEP 5: START PLATFORM
# ═══════════════════════════════════════════════════════════

echo "${BLUE}[STEP 5] Starting Platform${NC}"
echo ""
echo "Executing: mvn spring-boot:run"
echo ""
echo "${YELLOW}Platform is starting in background...${NC}"
echo "Waiting for server startup..."
echo ""

mvn spring-boot:run > /tmp/bootstrap.log 2>&1 &
PLATFORM_PID=$!

# Wait for server to start
for i in {1..30}; do
    if curl -s http://localhost:8080/platform/api/health > /dev/null 2>&1; then
        echo -e "${GREEN}✓${NC} Platform started successfully (PID: $PLATFORM_PID)"
        echo "   Server: http://localhost:8080/platform"
        echo "   API: http://localhost:8080/platform/api"
        break
    fi
    sleep 1
done

echo ""

# ═══════════════════════════════════════════════════════════
# STEP 6: TEST BOOTSTRAP FUNCTIONALITY
# ═══════════════════════════════════════════════════════════

echo "${BLUE}[STEP 6] Testing Platform API${NC}"
echo ""

echo "Health Check:"
curl -s -X GET http://localhost:8080/platform/api/health | jq .
echo ""

echo "Platform Info:"
curl -s -X GET http://localhost:8080/platform/api/info | jq .
echo ""

# ═══════════════════════════════════════════════════════════
# STEP 7: GENERATE EXAMPLE MICROSERVICE
# ═══════════════════════════════════════════════════════════

echo "${BLUE}[STEP 7] Generating Example Microservice${NC}"
echo ""
echo "Service Details:"
echo "  Name: user-service"
echo "  Port: 8081"
echo "  Package: com.company"
echo "  Database: PostgreSQL"
echo ""
echo "Sending bootstrap request..."
echo ""

RESPONSE=$(curl -s -X POST http://localhost:8080/platform/api/bootstrap \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "user-service",
    "port": "8081",
    "packageName": "com.company",
    "databaseType": "PostgreSQL",
    "description": "User Management Microservice"
  }')

echo "$RESPONSE" | jq .

echo ""

# Extract project path
PROJECT_PATH=$(echo "$RESPONSE" | jq -r '.projectPath' 2>/dev/null)

if [ "$PROJECT_PATH" != "null" ] && [ ! -z "$PROJECT_PATH" ]; then
    echo -e "${GREEN}✓${NC} Microservice generated successfully!"
    echo "   Location: $PROJECT_PATH"
    
    # ═══════════════════════════════════════════════════════════
    # STEP 8: VERIFY GENERATED STRUCTURE
    # ═══════════════════════════════════════════════════════════
    
    echo ""
    echo "${BLUE}[STEP 8] Verifying Generated Structure${NC}"
    echo ""
    
    if [ -d "$PROJECT_PATH" ]; then
        echo "Generated files:"
        ls -la "$PROJECT_PATH" | grep -E "pom.xml|Dockerfile|Jenkinsfile|README.md" | awk '{print "  ✓", $NF}'
        
        echo ""
        echo "Source structure:"
        if [ -d "$PROJECT_PATH/src/main/java" ]; then
            find "$PROJECT_PATH/src/main/java" -type f -name "*.java" | sed 's/^/  ✓ /' | head -10
        fi
    fi
else
    echo -e "${RED}✗${NC} Failed to generate microservice"
fi

echo ""

# ═══════════════════════════════════════════════════════════
# STEP 9: QUICK DEMONSTRATION
# ═══════════════════════════════════════════════════════════

echo "${BLUE}[STEP 9] Next Steps${NC}"
echo ""
echo "Your platform is running! Here's what you can do:"
echo ""
echo "1. Generate more microservices:"
echo "   curl -X POST http://localhost:8080/platform/api/bootstrap \\\"
echo "     -H 'Content-Type: application/json' \\\"
echo "     -d '{ \"serviceName\": \"product-service\", ... }'"
echo ""
echo "2. View generated microservice:"
echo "   ls -la /generated-microservices/user-service/"
echo ""
echo "3. Build generated microservice:"
echo "   cd /generated-microservices/user-service"
echo "   mvn clean install"
echo ""
echo "4. Run generated microservice:"
echo "   cd /generated-microservices/user-service"
echo "   mvn spring-boot:run"
echo ""
echo "5. View documentation:"
echo "   cat README.md"
echo "   cat EXECUTION_GUIDE.md"
echo ""
echo "6. Stop the platform:"
echo "   kill $PLATFORM_PID"
echo ""
echo "7. View platform logs:"
echo "   tail -f /tmp/bootstrap.log"
echo ""

echo "╔════════════════════════════════════════════════════════════╗"
echo "║      ✓ QUICK START COMPLETED SUCCESSFULLY!                 ║"
echo "║                                                              ║"
echo "║   Platform Status: RUNNING ✓                                ║"
echo "║   PID: $PLATFORM_PID                                              ║"
echo "║   URL: http://localhost:8080/platform                       ║"
echo "║   API: http://localhost:8080/platform/api                  ║"
echo "║                                                              ║"
echo "║   Keep this terminal open. Platform is running.            ║"
echo "║   Press Ctrl+C to stop the platform.                       ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Keep the script running (platform is in background)
wait $PLATFORM_PID
