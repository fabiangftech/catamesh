package dev.catamesh.infrastructure.config;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;

public class YAMLConfig {

    public ObjectMapper yamlMapper() {
        return new ObjectMapper(new YAMLFactory());
    }
}
