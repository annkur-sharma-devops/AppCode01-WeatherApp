pipeline {
    agent any

    environment {
        SONAR_TOKEN = credentials('sonarqube-token-id')
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main',
                    url: 'https://github.com/annkur-sharma-devops/AppCode01-WeatherApp.git',
                    credentialsId: 'github-token-10-aug-2025'
            }
        }

        stage('Build with Maven') {
            steps {
                bat 'mvn clean verify install'
            }
        }

        stage('Test with Maven') {
            steps {
                bat 'mvn clean test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('MySonarQubeServer') {
                    bat 'sonar-scanner'
                }
            }
        }

        stage('Upload to Nexus') {
            steps {
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: 'localhost:8899',
                    groupId: 'com.example',
                    version: "1.2.${BUILD_NUMBER}",
                    repository: 'maven-release-repo-weather-app',
                    credentialsId: 'nexus-credentials',
                    artifacts: [[
                        artifactId: 'weather-app',
                        type: 'war',
                        classifier: '',
                        file: 'target/weather-app.war'
                    ]]
                )
            }
            steps {
                bat 'echo Copying WAR to Nexus Repository'
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                bat """
                echo Copying WAR directly from Jenkins workspace
                set BUILD_NUM=${BUILD_NUMBER}
                set ARTIFACT_URL=http://localhost:8899/repository/maven-release-repo-weather-app/com/example/weather-app-1.2/1.2.%BUILD_NUM%/weather-app-1.2.%BUILD_NUM%.war

                echo Downloading from Nexus: %ARTIFACT_URL%
                curl -o %WORKSPACE%\\weather-app.war %ARTIFACT_URL%

                echo Copying to Tomcat webapps
                copy /Y %WORKSPACE%\\weather-app.war C:\\InstalledSoftwares\\apache-tomcat-9.0.108\\webapps\\
                """
            }
        }
    }

    post {
        success {
            echo 'Build, Upload & Deploy completed successfully!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
