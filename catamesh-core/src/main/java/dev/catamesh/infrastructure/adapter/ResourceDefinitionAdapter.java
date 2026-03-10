package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.exception.InvalidInputException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.SchemaVersion;
import tools.jackson.databind.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public final class ResourceDefinitionAdapter {
    private ResourceDefinitionAdapter(){}

    public static ResourceDefinition toResourceDefinition(ResultSet resultSet, ObjectMapper jsonMapper) throws SQLException {
        try {
            Map<String, Object> configJson = jsonMapper.readValue(resultSet.getString("config"), Map.class);
            return new ResourceDefinition(
                    SchemaVersion.fromValue(resultSet.getString("schema_version")),
                    resultSet.getString("version"),
                    configJson
            );
        } catch (InvalidInputException ex) {
            throw new MappingException("Invalid schema version in resource_definition row", ex);
        }
    }

    public static String toConfigJson(ResourceDefinition definition, ObjectMapper jsonMapper) {
        return jsonMapper.writeValueAsString(definition.getConfig());
    }
}
