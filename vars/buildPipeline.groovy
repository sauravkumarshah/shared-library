def call(Map pipelineParams = [:]) {
    pipeline {
        agent any
        environment {
            PROJECT_NAME = "${pipelineParams.projectName ?: 'Default-Project'}"  // Correct usage of string interpolation
        }
        stages {
            stage('Checkout') {
                steps {
                    checkout scm
                }
            }
            stage('Build') {
                steps {
                    script {
                        echo "Building project: ${PROJECT_NAME}"
                        sh './gradlew clean build -x test'
                    }
                }
            }
            stage('Test') {
                steps {
                    script {
                        echo "Running tests for ${PROJECT_NAME}"
                        sh './gradlew test'
                    }
                }
            }
            stage('Build Artifacts') {
                steps {
                    script {
                        echo "Building artifacts for ${PROJECT_NAME}"
                    }
                }
            }
            stage('Publish Artifacts') {
                steps {
                    script {
                        echo "Publishing artifacts for ${PROJECT_NAME}"
                    }
                }
            }
        }
        post {
            always {
                echo "Pipeline completed for ${PROJECT_NAME}"
            }
        }
    }
}
