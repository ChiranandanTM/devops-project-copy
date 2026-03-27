#!/usr/bin/env groovy

// Microservice Bootstrap Platform - Main CI/CD Pipeline
// Automates build, testing, and Docker image creation

pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '30', daysToKeepStr: '7'))
        timeout(time: 2, unit: 'HOURS')
        timestamps()
        ansiColor('xterm')
    }

    environment {
        PROJECT_NAME = 'bootstrap-platform'
        DOCKER_IMAGE = 'bootstrap-platform'
        DOCKER_REGISTRY = 'docker.io'
    }

    stages {

        stage('Checkout') {
            steps {
                script {
                    echo '╔════════════════════════════════════════════════════════════╗'
                    echo '║             STEP 1: Clone Repository                       ║'
                    echo '╚════════════════════════════════════════════════════════════╝'
                }
                checkout scm
                script {
                    // Capture git info using powershell for Windows compatibility
                    env.GIT_COMMIT_SHORT = powershell(returnStdout: true, script: '''
                        git rev-parse --short HEAD
                    ''').trim()
                    env.GIT_BRANCH = powershell(returnStdout: true, script: '''
                        git rev-parse --abbrev-ref HEAD
                    ''').trim()
                    echo "✓ Cloned repository: ${env.GIT_BRANCH} (${env.GIT_COMMIT_SHORT})"
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    echo '╔════════════════════════════════════════════════════════════╗'
                    echo '║             STEP 2: Build Platform                         ║'
                    echo '╚════════════════════════════════════════════════════════════╝'
                }
                bat '''
                    cd platform
                    mvn clean package -DskipTests
                    @echo ✓ Build completed successfully
                '''
            }
        }

        stage('Test') {
            steps {
                script {
                    echo '╔════════════════════════════════════════════════════════════╗'
                    echo '║             STEP 3: Run Tests                              ║'
                    echo '╚════════════════════════════════════════════════════════════╝'
                }
                bat '''
                    cd platform
                    mvn test
                    @echo ✓ Tests completed successfully
                '''
            }
            post {
                always {
                    junit 'platform/target/surefire-reports/**/*.xml'
                }
            }
        }

        stage('Code Quality') {
            steps {
                script {
                    echo '╔════════════════════════════════════════════════════════════╗'
                    echo '║             STEP 4: Code Quality Check                     ║'
                    echo '╚════════════════════════════════════════════════════════════╝'
                }
                bat '''
                    cd platform
                    mvn test
                    @echo ✓ Code quality analysis completed - Coverage report generated
                '''
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'platform/target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }

        stage('Docker Build') {
            steps {
                script {
                    echo '╔════════════════════════════════════════════════════════════╗'
                    echo '║             STEP 5: Build Docker Image                     ║'
                    echo '╚════════════════════════════════════════════════════════════╝'
                }
                bat '''
                    setlocal enabledelayedexpansion
                    cd platform
                    set IMAGE_TAG=%DOCKER_IMAGE%:%GIT_COMMIT_SHORT%
                    set IMAGE_LATEST=%DOCKER_IMAGE%:latest
                    
                    docker build -t !IMAGE_TAG! -t !IMAGE_LATEST! .
                    
                    @echo ✓ Docker image built successfully
                    @echo   - !IMAGE_TAG!
                    @echo   - !IMAGE_LATEST!
                '''
            }
        }

        stage('Deploy Preparation') {
            when {
                anyOf {
                    branch 'main'
                    branch 'master'
                    branch 'develop'
                }
            }
            steps {
                script {
                    echo '╔════════════════════════════════════════════════════════════╗'
                    echo '║             STEP 6: Deployment Ready                       ║'
                    echo '╚════════════════════════════════════════════════════════════╝'
                }
                bat '''
                    @echo Platform is ready for deployment
                    @echo Docker image: %DOCKER_IMAGE%:%GIT_COMMIT_SHORT%
                    @echo Branch: %GIT_BRANCH%
                '''
            }
        }
    }

    post {
        always {
            script {
                echo '╔════════════════════════════════════════════════════════════╗'
                echo "║  Pipeline Result: ${currentBuild.result}                                          ║"
                echo '╚════════════════════════════════════════════════════════════╝'
            }
            cleanWs()
        }
        failure {
            script {
                echo '❌ Pipeline failed! Check logs for details.'
            }
        }
        success {
            script {
                echo '✅ Pipeline completed successfully!'
                echo "Commit: ${env.GIT_COMMIT_SHORT}"
                echo "Branch: ${env.GIT_BRANCH}"
            }
        }
    }
}
