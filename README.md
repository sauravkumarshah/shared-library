# Shared Library for Jenkins Pipelines

This library contains reusable pipeline templates and configurations for JTE.

## Structure
- `resources/`: Contains templates and default configurations.
- `vars/`: Contains reusable helper functions.

## Usage
Include the library in your Jenkinsfile:
```groovy
@Library('shared-library') _
buildPipeline(projectName: 'service-name')
