def call(Map pipelineParams = [:]) {
    pipeline {
        agent any
        environment {
            PROJECT_NAME = "${pipelineParams.projectName ?: 'Default-Project'}"  // Get the project name for the services to build the Jenkins pipeline.
            PIPELINE_CONFIG = '' // Declare the environment variable to store the parsed pipeline config.
        }
        stages {
            stage('Checkout') {
                steps {
                    script {
                        // Read the YAML file using the readYaml step
                        def pipelineConfig = readYaml file: 'pipeline_config.yml'

                        // Set the pipeline config as an environment variable (as a string)
                        PIPELINE_CONFIG = pipelineConfig

                        // Print the pipeline configuration
                        echo "Pipeline configuration: ${PIPELINE_CONFIG}"
                        echo "Stages: ${PIPELINE_CONFIG.stages}"
                        echo "Environment: ${PIPELINE_CONFIG.variables.environment}"
                    }
                    checkout scm
                }
            }
            stage('Build') {
                steps {
                    script {
                        // Access the stages and variables from the previously set environment variable
                        echo "Accessing Stages in another stage: ${PIPELINE_CONFIG.stages}"
                        echo "Environment in another stage: ${PIPELINE_CONFIG.variables.environment}"

                        echo "Building project: ${PROJECT_NAME}"
                        sh 'chmod +x ./gradlew'
                        sh './gradlew clean build -x test'
                    }
                }
                post {
                    success {
                        publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: false, reportDir: 'build/reports/spotbugs', reportFiles: 'main.html, test.html', reportName: 'FindBugs Reports'])
                        publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: false, reportDir: 'build/reports/pmd', reportFiles: 'main.html, test.html', reportName: 'PMD Reports'])
                    }
                    failure {
                        echo 'Failed to execute'
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
                post {
                    success {
                        publishHTML(target: [allowMissing: true, alwaysLinkToLastBuild: true, keepAll: false, reportDir: 'build/jacocoHtml', reportFiles: 'index.html', reportName: 'Test Coverage Report'])
                    }
                    failure {
                        echo 'Failed to execute'
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
