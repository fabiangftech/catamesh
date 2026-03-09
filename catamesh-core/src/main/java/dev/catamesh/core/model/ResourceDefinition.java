package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceDefinition {
    private final SchemaVersion schemaVersion;
    private final String version;
    private final Map<String, Object> config;
    public ResourceDefinition(SchemaVersion schemaVersion, String version, Map<String, Object> config) {
        this.schemaVersion = schemaVersion;
        this.version = version;
        this.config = config;
    }

    public SchemaVersion getSchemaVersion() {
        return schemaVersion;
    }

    public String getVersion() {
        return version;
    }
    public Map<String, Object> getConfig() {
        return config;
    }
}
