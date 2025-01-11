def call(Map pipelineParams = [:]) {
    pipeline {
        agent any
        environment {
            PROJECT_NAME = "${pipelineParams.projectName ?: 'Default-Project'}"  // Correct usage of string interpolation
            PIPELINE_CONFIG = readYaml file: 'pipeline_config.yml'
        }
        stages {
            stage('Checkout') {
                steps {
                    echo "Pipeline configuration: ${env.PIPELINE_CONFIG}"
                    echo "Checking for 'test' in stages: ${env.PIPELINE_CONFIG.stages}"
                    echo "Does 'test' exist in stages? ${env.PIPELINE_CONFIG.stages.contains('test')}"
                    // Proceed with checkout step
                    checkout scm
                }
            }
            stage('Build') {
                steps {
                    script {
                        echo "Building project: ${env.PROJECT_NAME}"
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
                        echo "Running tests for ${env.PROJECT_NAME}"
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
                        echo "Building artifacts for ${env.PROJECT_NAME}"
                    }
                }
            }
            stage('Publish Artifacts') {
                steps {
                    script {
                        echo "Publishing artifacts for ${env.PROJECT_NAME}"
                    }
                }
            }
        }
        post {
            always {
                echo "Pipeline completed for ${env.PROJECT_NAME}"
            }
        }
    }
}
