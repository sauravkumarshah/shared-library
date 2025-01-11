# Shared Library for Jenkins Pipelines

This repository contains a Jenkins shared library for managing and streamlining CI/CD pipelines across multiple projects. The shared library provides reusable pipeline steps, utilities, and configurations, allowing teams to standardize their Jenkins pipelines and reduce duplication.

---

## Features

- **Pipeline Configuration via YAML**: Centralized pipeline configuration managed in `pipeline_config.yml` files.
- **Reusable Steps**: Common build, test, deploy, and report generation stages.
- **Dynamic Stage Execution**: Execute stages conditionally based on the configuration file.
- **Integration with Tools**:
    - SpotBugs, PMD, and JaCoCo reports.
    - Environment-specific configurations.
    - Artifact management.
- **Customizable Pipelines**: Teams can define stages and variables per service using YAML.

---

## Structure

### File Structure

```plaintext
jenkins-shared-library/
├── vars/
│   ├── buildPipeline.groovy        # Entry point for custom pipeline execution
│   ├── utility.groovy              # Utility functions used across pipelines
├── resources/
│   ├── pipeline_config_defaults.yml # Default pipeline configuration
├── README.md                       # Documentation for the shared library
```

## Key Files

- **vars/buildPipeline.groovy**: The main pipeline logic that integrates the shared library with Jenkins pipelines.
- **vars/utility.groovy**: Utility functions for common tasks like environment variable handling, YAML parsing, etc.
- **resources/pipeline_config_defaults.yml**: A default configuration file for pipelines that teams can override.

---

## Usage

### Integrating with a Jenkins Pipeline
1. Add the shared library to Jenkins:
   - Go to **Jenkins > Manage Jenkins > Configure System > Global Trusted Pipeline Libraries.**

   - Add a new library with:

     - **Library Name**: shared-library

     - **Default Version**: (Branch/tag to use, e.g., main).

     - **Retrieval Method**: SCM and provide the Git repository URL.

2. Add a **pipeline_config.yml** file in the root of your service repository:
```
    stages:
     - checkout
     - build
     - test
     - build_artifacts
     - publish_artifacts
    variables:
     environment: "dev"
     artifact_name: "service-a-artifact"
```

3. Use the shared library in your **Jenkinsfile**:
```
  @Library('shared-library') _  // Load the shared library
  
  buildPipeline(projectName: 'service-a')  // Call the shared library method with parameters
```
---

## Default Configuration

The default configuration is stored in **pipeline_config_defaults.yml** in the **shared library**. This file is used when no **pipeline_config.yml** is found in the service repository.

---

### Customization

Teams can override stages or variables by defining a pipeline_config.yml file in their repository. The shared library handles merging with the defaults, ensuring flexibility while maintaining standardization.

---

### Debugging Tips

- Ensure the shared library is correctly loaded by Jenkins.

- Use echo statements to debug pipeline stages and configuration values.

- Verify that your pipeline_config.yml file is correctly formatted and accessible.

- Use script blocks for complex conditional logic in pipelines.

---