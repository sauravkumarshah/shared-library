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
                    }
                }
            }
            stage('Test') {
                steps {
                    script {
                        echo "Running tests for ${PROJECT_NAME}"
                    }
                }
            }
            stage('Deploy') {
                steps {
                    script {
                        echo "Deploying ${PROJECT_NAME}"
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
