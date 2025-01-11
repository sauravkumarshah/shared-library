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
                    script {
                        // Read YAML file explicitly inside the script block then only correct classes is initializing else it would store all the contents of the yml into String only.
                        PIPELINE_CONFIG_NEW =  new groovy.yaml.YamlSlurper().parseText(PIPELINE_CONFIG)
                        echo "Pipeline configuration: ${PIPELINE_CONFIG_NEW}"
                        echo "Type of PIPELINE_CONFIG: ${PIPELINE_CONFIG_NEW.getClass()}"
                        echo "Stages: ${PIPELINE_CONFIG['stages']}"  // Accessing stages using map syntax
                    }
                    checkout scm
                }
            }
            stage('Build') {
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
