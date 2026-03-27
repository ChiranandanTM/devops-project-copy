@echo off
REM ════════════════════════════════════════════════════════════
REM  MICROSERVICE BOOTSTRAP AUTOMATION PLATFORM
REM  QUICK START GUIDE - 5 MINUTE SETUP (Windows)
REM ════════════════════════════════════════════════════════════

setlocal enabledelayedexpansion

echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║        MICROSERVICE BOOTSTRAP PLATFORM v1.0.0              ║
echo ║              Enterprise DevOps Automation                  ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

REM ═════════════════════════════════════════════════════════════
REM STEP 1: VERIFY PREREQUISITES
REM ═════════════════════════════════════════════════════════════

echo [STEP 1] Verifying Prerequisites...
echo.

where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    exit /b 1
) else (
    echo ✓ Java is installed
    java -version
)

where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    exit /b 1
) else (
    echo ✓ Maven is installed
    mvn --version
)

where git >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Git is not installed or not in PATH
    exit /b 1
) else (
    echo ✓ Git is installed
    git --version
)

echo.

REM ═════════════════════════════════════════════════════════════
REM STEP 2: BUILD PLATFORM
REM ═════════════════════════════════════════════════════════════

echo [STEP 2] Building Platform...
echo.

call mvn clean install
if %errorlevel% neq 0 (
    echo ERROR: Build failed
    exit /b 1
)

echo ✓ Build completed successfully
echo.

REM ═════════════════════════════════════════════════════════════
REM STEP 3: RUN TESTS AND CODE QUALITY
REM ═════════════════════════════════════════════════════════════

echo [STEP 3] Running Tests and Code Quality Checks...
echo.

call mvn clean test jacoco:report jacoco:check
if %errorlevel% neq 0 (
    echo ERROR: Tests or code quality check failed
    exit /b 1
)

echo ✓ Tests passed with 80%% coverage
echo.

REM ═════════════════════════════════════════════════════════════
REM STEP 4: START PLATFORM
REM ═════════════════════════════════════════════════════════════

echo [STEP 4] Starting Platform...
echo.
echo Platform will start in the background. This window will show platform output.
echo.

start cmd /k "cd %CD% && mvn spring-boot:run"

timeout /t 5 /nobreak

echo.
echo ✓ Platform is starting at http://localhost:8080/platform
echo.

REM ═════════════════════════════════════════════════════════════
REM STEP 5: TEST API
REM ═════════════════════════════════════════════════════════════

echo [STEP 5] Testing Platform API...
echo.

curl -s -X GET http://localhost:8080/platform/api/health
echo.

echo.

REM ═════════════════════════════════════════════════════════════
REM STEP 6: GENERATE EXAMPLE MICROSERVICE
REM ═════════════════════════════════════════════════════════════

echo [STEP 6] Generating Example Microservice...
echo.

curl -X POST http://localhost:8080/platform/api/bootstrap ^
  -H "Content-Type: application/json" ^
  -d "{\"serviceName\": \"user-service\", \"port\": \"8081\", \"packageName\": \"com.company\", \"databaseType\": \"PostgreSQL\", \"description\": \"User Management Microservice\"}"

echo.
echo.

echo ╔════════════════════════════════════════════════════════════╗
echo ║       ✓ QUICK START COMPLETED!                             ║
echo ║                                                              ║
echo ║    Platform is running: http://localhost:8080/platform    ║
echo ║    Check the background terminal for platform logs        ║
echo ║                                                              ║
echo ║    Documentation:                                           ║
echo ║    - README.md - Overview and features                     ║
echo ║    - EXECUTION_GUIDE.md - Detailed setup guide            ║
echo ║    - COMPLETE_DOCUMENTATION.md - Full technical docs      ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

pause
