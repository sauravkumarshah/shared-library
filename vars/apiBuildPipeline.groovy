def call(Map pipelineParams = [:]) {
    pipeline {
        agent any
        environment {
            PROJECT_NAME = "${pipelineParams.projectName ?: 'Default-Project'}"  // Get the project name for the services to build the Jenkins pipeline.
            PIPELINE_CONFIG = '' // Declare the environment variable to store the parsed pipeline config.
            STUDENT_NAME = "${pipelineParams.studentName ?: 'Student name not found'}"
        }
        stages {
            stage('Checkout') {
                steps {
                    script {
                        echo "Student name : ${STUDENT_NAME}"
                        PIPELINE_CONFIG = utility.loadPipelineConfig()

                        // Print the pipeline configuration
                        echo "Pipeline configuration: ${PIPELINE_CONFIG}"

                        // Print the stages to complete in building the Jenkins pipeline for Microservices.
                        echo "Stages: ${PIPELINE_CONFIG.stages}"
                        echo "Variables: ${PIPELINE_CONFIG.variables}"
                        echo "Artifact name: ${PIPELINE_CONFIG.variables.artifact_name}"
                    }
                    checkout scm
                }
            }
            stage('Build') {
                when {
                    expression {
                        return PIPELINE_CONFIG.stages.find { it.name == 'build' }?.enabled == true
                    }
                }
                steps {
                    script {
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
                when {
                    expression {
                        return PIPELINE_CONFIG.stages.find { it.name == 'test' }?.enabled == true
                    }
                }
                steps {
                    script {
                        echo "Running tests for ${PROJECT_NAME}"
                        sh 'chmod +x ./gradlew'
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
            stage('Static Api Documentation') {
                when {
                    expression {
                        return PIPELINE_CONFIG.stages.find { it.name == 'static_api_documentation' }?.enabled == true
                    }
                }
                steps {
                    script {
                        echo "Generate Static Api Documentation for ${PROJECT_NAME}"
                    }
                }
            }
            stage('Generate Client Jar') {
                when {
                    expression {
                        return PIPELINE_CONFIG.stages.find { it.name == 'generate_client_jar' }?.enabled == true
                    }
                }
                steps {
                    script {
                        echo "Generate Client jar for ${PROJECT_NAME}"
                    }
                }
            }
            stage('Build Client Jar') {
                when {
                    expression {
                        return PIPELINE_CONFIG.stages.find { it.name == 'build_client_jar' }?.enabled == true
                    }
                }
                steps {
                    script {
                        echo "Build Client jar for ${PROJECT_NAME}"
                    }
                }
            }
            stage('Build Artifacts') {
                when {
                    expression {
                        return PIPELINE_CONFIG.stages.find { it.name == 'build_artifacts' }?.enabled == true
                    }
                }
                steps {
                    script {
                        echo "Building artifacts for ${PROJECT_NAME}"
                    }
                }
            }
            stage('Publish Artifacts') {
                when {
                    expression {
                        return PIPELINE_CONFIG.stages.find { it.name == 'publish_artifacts' }?.enabled == true
                    }
                }
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
