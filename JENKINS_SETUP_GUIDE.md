# 🚀 Jenkins CI/CD Setup Guide
## Microservice Bootstrap Automation Platform

**Version:** 1.0.0  
**Date:** March 2026

---

## 🎯 Important Note: Windows vs Unix

**This Jenkinsfile is configured for Windows Jenkins agents.**

If running Jenkins on Linux/Mac, replace all `bat` commands with `sh`:
- `bat '''` → `sh '''`
- `@echo` → `echo`
- `%VAR%` → `${VAR}`

---

## 📋 Overview

This guide walks you through setting up Jenkins for automated CI/CD with the Microservice Bootstrap Platform. Jenkins will automatically:
- Clone your repository
- Build the platform
- Run unit tests
- Check code quality (JaCoCo coverage reports)
- Build Docker images
- Prepare for deployment

---

## ✅ STEP 1 — Install Jenkins

### Option A: Docker Installation (Recommended)
```bash
# Run Jenkins in Docker
docker run -d \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --name jenkins \
  jenkins/jenkins:lts

# Get initial admin password
docker logs jenkins 2>&1 | grep -A 5 "Please use"
```

### Option B: Direct Installation
1. Download from: https://jenkins.io/download
2. Follow platform-specific installation instructions
3. Run: `java -jar jenkins.war`
4. Open: http://localhost:8080

**✓ Access Jenkins**
```
URL: http://localhost:8080
```

---

## ✅ STEP 2 — Unlock Jenkins

### Initial Setup

1. **Copy unlock password:**
   - Check Jenkins console output for the initial admin password
   - Or find it at: `/var/jenkins_home/secrets/initialAdminPassword`

2. **Paste password** in Jenkins unlock screen

3. **Install suggested plugins:**
   - Click "Install suggested plugins"
   - Wait for installation to complete

4. **Create admin user:**
   - Username: `admin`
   - Password: (your choice)
   - Email: (your choice)

5. **Confirm Jenkins URL:**
   - Default: `http://localhost:8080/`
   - Click "Save and Continue"

**✓ Jenkins is now unlocked!**

---

## ✅ STEP 3 — Install Required Plugins

Go to: **Manage Jenkins** → **Manage Plugins** → **Available**

Search and install:
- ✅ **Pipeline** (if not already installed)
- ✅ **Git** (SCM integration)
- ✅ **HTML Publisher** (for test/coverage reports)
- ✅ **AnsiColor** (colored console output)
- ✅ **Timestamper** (add timestamps to logs)
- ✅ **Docker Pipeline** (Docker integration - optional)

---

## ✅ STEP 4 — Create Pipeline Job

### New Pipeline Job

1. **Click:** "New Item" (top-left)

2. **Enter job name:** 
   ```
   devops-project
   ```

3. **Select:** "Pipeline"

4. **Click:** "OK"

---

## ✅ STEP 5 — Configure Pipeline Job

### Pipeline Configuration

In the job configuration page:

1. **Go to:** "Pipeline" section (scroll down)

2. **Select:** "Pipeline script from SCM"

3. **Choose SCM:** "Git"

4. **Repository URL:**
   ```
   https://github.com/your-org/devops-project.git
   ```
   *(Replace with your actual GitHub repo URL)*

5. **Branch:**
   ```
   */main
   ```
   *(or your default branch)*

6. **Script Path:**
   ```
   Jenkinsfile
   ```
   *(Default location - no change needed)*

7. **Click:** "Save"

**✓ Pipeline is now configured!**

---

## ✅ STEP 6 — Connect GitHub (Optional - For Webhooks)

### GitHub Webhook Configuration

1. **In GitHub repository:**
   - Go to: Settings → Webhooks → Add webhook

2. **Webhook settings:**
   - **Payload URL:** `http://your-jenkins-url:8080/github-webhook/`
   - **Content type:** `application/json`
   - **Events:** Push events, Pull request events
   - **Active:** ✓ Checked

3. **Click:** "Add webhook"

4. **In Jenkins:**
   - Build will trigger automatically on GitHub push
   - Or manually click "Build Now"

---

## ✅ STEP 7 — Run Pipeline

### Manual Pipeline Execution

1. **Open your job:** Jenkins Dashboard → devops-project

2. **Click:** "Build Now" (left sidebar)

3. **Monitor execution:**
   - Click the build number (e.g., #1)
   - Click "Console Output"

### Expected Stages

```
✅ Checkout
   └─ Clones repository from GitHub

✅ Build
   └─ Runs: mvn clean package -DskipTests
   └─ Compiles platform code

✅ Test
   └─ Runs: mvn test
   └─ Executes JUnit tests
   └─ Publishes test reports

✅ Code Quality
   └─ Runs: mvn clean test jacoco:report
   └─ Generates coverage reports
   └─ Publishes JaCoCo coverage reports (report-only, no enforcement)

✅ Docker Build
   └─ Builds Docker image: bootstrap-platform:latest
   └─ Tags with git commit hash

✅ Deploy Preparation
   └─ Readies for deployment
   └─ (Only on main/master/develop branches)
```

---

## 📊 View Reports

### Test Reports
1. In Build page, click: **"Surefire Test Report"**
2. View unit test results

### Code Coverage
1. In Build page, click: **"JaCoCo Coverage Report"**
2. View coverage metrics (minimum 80% required)

---

## 🔧 Troubleshooting

### "Build Failed" - Git Clone Issue
**Solution:**
- Verify repository URL
- Check GitHub credentials in Jenkins
- Manage Jenkins → Configure System → GitHub

### "Maven not found"
**Solution:**
- Install Maven plugin: Manage Jenkins → Manage Plugins
- Or ensure Maven is in system PATH

### "Docker not found"
**Solution:**
- Install Docker on Jenkins server
- Ensure Jenkins user has Docker permissions: `sudo usermod -aG docker jenkins`

### "Test Failed: Cannot read properties of null"
**Solution:**
- Check platform/src directory has Java source files
- Run locally first: `cd platform && mvn test`

---

## 📸 Demo Script

**What to say in demo:**

> "We've implemented a comprehensive CI/CD pipeline using Jenkins that automates the entire build, test, and deployment process. When developers push code to GitHub, Jenkins automatically:
>
> 1. **Clones** the latest code
> 2. **Builds** the platform using Maven
> 3. **Tests** with JUnit (all tests must pass)
> 4. **Validates** code quality (80% coverage minimum)
> 5. **Packages** as Docker image
> 6. **Prepares** for deployment
>
> This ensures every committed code is tested, validated, and ready for production. The entire pipeline runs in just a few minutes, providing fast feedback to developers."

---

## 📋 Success Checklist

- [ ] Jenkins installed and running
- [ ] Initial admin user created
- [ ] Required plugins installed
- [ ] Pipeline job "devops-project" created
- [ ] Git repository connected
- [ ] Jenkinsfile added to project root
- [ ] First build executed successfully
- [ ] All stages passed (Checkout, Build, Test, Docker Build)
- [ ] Test reports view working
- [ ] Coverage reports view working
- [ ] GitHub webhook configured (optional)

---

## 🎯 Next Steps

1. **Run build regularly:** Enable GitHub webhook for automatic triggers
2. **Monitor metrics:** Check coverage reports after each build
3. **Extend pipeline:** Add Docker push, Kubernetes deploy stages
4. **Secure credentials:** Use Jenkins credentials store for sensitive data
5. **Email notifications:** Add post-build email notifications

---

## 📚 Learn More

- **Jenkins Documentation:** https://www.jenkins.io/doc/
- **Pipeline Syntax:** https://www.jenkins.io/doc/book/pipeline/
- **GitHub Webhooks:** https://docs.github.com/en/developers/webhooks-and-events/webhooks

---

**Setup Complete! 🚀**  
Your CI/CD pipeline is now ready for automated builds and testing.
