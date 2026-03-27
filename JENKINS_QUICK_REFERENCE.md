# 🚀 Jenkins Quick Reference Card

## 1️⃣ START JENKINS

```bash
# Docker (recommended)
docker run -d -p 8080:8080 -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  jenkins/jenkins:lts

# Then access: http://localhost:8080
```

## 2️⃣ UNLOCK JENKINS
- Get password from console logs
- Paste in Jenkins UI
- Install suggested plugins
- Create admin user

## 3️⃣ CREATE JOB
```
Click: New Item
Name: devops-project
Type: Pipeline
Click: OK
```

## 4️⃣ CONFIGURE PIPELINE
```
Pipeline: Pipeline script from SCM
SCM: Git
Repository: https://github.com/YOUR-ORG/devops-project.git
Branch: */main
Script Path: Jenkinsfile
Click: Save
```

## 5️⃣ RUN PIPELINE
```
Click: Build Now
Watch: Console Output
```

## 🎯 PIPELINE STAGES
```
✅ Checkout  - Copy code from GitHub
✅ Build     - Compile with Maven
✅ Test      - Run JUnit tests
✅ Quality   - Verify 80% code coverage
✅ Docker    - Build container image
✅ Ready     - Prepared for deployment
```

## 📊 VIEW REPORTS
| Report | Location |
|--------|----------|
| Tests | Build → Surefire Test Report |
| Coverage | Build → JaCoCo Coverage Report |
| Console | Build → Console Output |

## ⚠️ QUICK FIXES

| Issue | Solution |
|-------|----------|
| Git clone fails | Check repo URL & credentials |
| Maven not found | Install Maven plugin |
| Docker not found | Install Docker on server |
| Tests fail | Run `cd platform && mvn test` locally |

## 🔗 IMPORTANT URLS
```
Jenkins:     http://localhost:8080
Job:         http://localhost:8080/job/devops-project
Plugins:     http://localhost:8080/manage/pluginManager/
Configure:   http://localhost:8080/manage/configure
```

## 💡 DEMO SCRIPT
> "Our CI/CD pipeline automatically tests every code commit, validates quality metrics, builds Docker images, and prepares for deployment—all in minutes."

---

**See:** `JENKINS_SETUP_GUIDE.md` for complete setup instructions
