package dev.catamesh.infrastructure.config;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;

public final class YAMLConfig {

    private YAMLConfig(){
        // do nothing
    }
    public static ObjectMapper yamlMapper() {
        return new ObjectMapper(new YAMLFactory());
    }
}
