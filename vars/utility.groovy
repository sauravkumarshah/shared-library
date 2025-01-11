def parseYaml(String yamlContent) {
    // Parse a YAML string and return a Map
    def yaml = new org.yaml.snakeyaml.Yaml()
    return yaml.load(yamlContent)
}

def loadPipelineConfig(String filePath = 'pipeline_config.yml') {
    // Load the pipeline configuration from a YAML file
    if (fileExists(filePath)) {
        return readYaml(file: filePath)
    } else {
        echo "Pipeline configuration file not found. Using defaults."
        return readYaml(file: "${libraryResource('pipeline_config_defaults.yml')}")
    }
}

def mergeConfigs(Map defaultConfig, Map userConfig) {
    // Merge default configuration with user configuration, with userConfig taking precedence
    return defaultConfig + userConfig.collectEntries { key, value ->
        [key, value instanceof Map && defaultConfig[key] instanceof Map ? mergeConfigs(defaultConfig[key], value) : value]
    }
}

def sendSlackNotification(String channel, String message) {
    // Send a Slack notification
    slackSend(channel: channel, message: message)
}

def getCurrentBranch() {
    // Get the current Git branch
    return env.BRANCH_NAME ?: sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
}

return this
