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
                        // Check if pipeline_config.yml exists in the service repository
                        if (fileExists('pipeline_config.yml')) {
                            echo "Using pipeline_config.yml from service repository."
                            pipelineConfig = readYaml file: 'pipeline_config.yml'
                        } else {
                            echo "pipeline_config.yml not found. Using default configuration from shared library."
                            def defaultsFileContent = libraryResource('pipeline_config_defaults.yml')
                            pipelineConfig = readYaml text: defaultsFileContent
                        }

                        // Debugging: Print the configuration
                        echo "Pipeline configuration: ${pipelineConfig}"

                        // Set the pipeline config as an environment variable (as a string)
                        PIPELINE_CONFIG = pipelineConfig

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
                        return PIPELINE_CONFIG.stages.contains('static_api_documentation')
                    }
                }
                steps {
                    script {
                        echo "Generate Static Api Documentation for ${PROJECT_NAME}"

                    }
                }
            }
            stages {
                stage('Generate Client Jar') {
                    when {
                        expression {
                            return PIPELINE_CONFIG.stages.contains('generate_client_jar')
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
                            return PIPELINE_CONFIG.stages.contains('build_client_jar')
                        }
                    }
                    steps {
                        script {
                            echo "Build Client jar for ${PROJECT_NAME}"

                        }
                    }
                }
            }
            stage('Build Artifacts') {
                when {
                    expression {
                        return PIPELINE_CONFIG.stages.contains('build_artifacts')
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
                        return PIPELINE_CONFIG.stages.contains('publish_artifacts')
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
