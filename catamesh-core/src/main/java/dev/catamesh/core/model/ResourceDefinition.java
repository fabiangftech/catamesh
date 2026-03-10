package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceDefinition {
    private SchemaVersion schemaVersion;
    private String version;
    private Map<String, Object> config;

    public ResourceDefinition() {

    }

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

    public static boolean isSameVersionContent(ResourceDefinition current, ResourceDefinition candidate) {
        return Objects.equals(current.getSchemaVersion(), candidate.getSchemaVersion())
               && Objects.equals(current.getConfig(), candidate.getConfig());
    }

    public static List<String> immutableDifferences(ResourceDefinition current, ResourceDefinition candidate) {
        List<String> differences = new ArrayList<>();

        if (!Objects.equals(current.getSchemaVersion(), candidate.getSchemaVersion())) {
            differences.add("schemaVersion");
        }
        if (!Objects.equals(current.getConfig(), candidate.getConfig())) {
            differences.add("config");
        }

        return differences;
    }
}
