def call(Map pipelineParams = [:]) {
    pipeline {
        agent any
        environment {
            PROJECT_NAME = "${pipelineParams.projectName ?: 'Default-Project'}"  // Correct usage of string interpolation
            PIPELINE_CONFIG = readYaml file: 'pipeline_config.yml'
        }
        stages {
            stage('Checkout') {
                when {
                    expression {
                        return PIPELINE_CONFIG.stages.contains('checkout')
                    }
                }
                steps {
                    echo "Pipeline configuration: ${PIPELINE_CONFIG}"
                    echo "Checking for 'test' in stages: ${PIPELINE_CONFIG.stages}"
                    echo "Does 'test' exist in stages? ${PIPELINE_CONFIG.stages.contains('test')}"
                    // Proceed with checkout step
                    checkout scm
                }
            }
            stage('Build') {
                when {
                    expression {
                        return PIPELINE_CONFIG.stages.contains('build')
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
                        return PIPELINE_CONFIG.stages.contains('test')
                    }
                }
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
