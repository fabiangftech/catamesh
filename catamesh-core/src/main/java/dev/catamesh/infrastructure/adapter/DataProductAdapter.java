package dev.catamesh.infrastructure.adapter;


import dev.catamesh.core.exception.InvalidInputException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.model.*;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLWriteFeature;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DataProductAdapter {

    private static final String COLUMN_ID = "id";
    private static final String COLUMNS_SCHEMA_VERSION = "schema_version";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DISPLAY_NAME = "display_name";
    private static final String COLUMN_DOMAIN = "domain";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_KIND = "kind";
    private static final ObjectMapper YAML_WRITER = new ObjectMapper(
            YAMLFactory.builder()
                    .disable(YAMLWriteFeature.WRITE_DOC_START_MARKER)
                    .enable(YAMLWriteFeature.MINIMIZE_QUOTES)
                    .enable(YAMLWriteFeature.INDENT_ARRAYS_WITH_INDICATOR)
                    .build()
    );

    private DataProductAdapter() {
        // do nothing
    }

    public static DataProduct toDataProduct(ResultSet resultSet) throws SQLException {
        try {
            Metadata metadata = toMetadata(resultSet);
            Spec specification = toSpec(resultSet);
            SchemaVersion schemaVersion = SchemaVersion.fromValue(resultSet.getString(COLUMNS_SCHEMA_VERSION));
            return new DataProduct(schemaVersion, metadata, specification);
        } catch (InvalidInputException ex) {
            throw new MappingException("Invalid enum value in data_product row", ex);
        }
    }

    public static Metadata toMetadata(ResultSet resultSet) throws SQLException {
        return new Metadata(
                Key.create(resultSet.getString(COLUMN_ID)),
                resultSet.getString(COLUMN_NAME),
                resultSet.getString(COLUMN_DISPLAY_NAME),
                resultSet.getString(COLUMN_DOMAIN),
                resultSet.getString(COLUMN_DESCRIPTION)
        );
    }

    public static Spec toSpec(ResultSet resultSet) throws SQLException {
        try {
            return new Spec(DataProductKind.fromValue(resultSet.getString(COLUMN_KIND)), null);
        } catch (InvalidInputException ex) {
            throw new MappingException("Invalid data product kind in data_product row", ex);
        }
    }

    public static String toYaml(DataProduct dataProduct) {
        Map<String, Object> payload = new LinkedHashMap<>();
        putIfNotNull(payload, "schemaVersion", dataProduct.getSchemaVersion());
        putIfNotNull(payload, "metadata", toMetadataPayload(dataProduct.getMetadata()));
        putIfNotNull(payload, "spec", toSpecPayload(dataProduct.getSpec()));
        return YAML_WRITER.writeValueAsString(payload);
    }

    private static Map<String, Object> toMetadataPayload(Metadata metadata) {
        if (metadata == null) {
            return null;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        putIfNotNull(payload, "name", metadata.getName());
        putIfNotNull(payload, "displayName", metadata.getDisplayName());
        putIfNotNull(payload, "domain", metadata.getDomain());
        putIfNotNull(payload, "description", metadata.getDescription());
        return payload;
    }

    private static Map<String, Object> toSpecPayload(Spec spec) {
        if (spec == null) {
            return null;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        putIfNotNull(payload, "kind", spec.getKind());
        if (spec.getResources() != null) {
            List<Object> resources = new ArrayList<>();
            for (Resource resource : spec.getResources()) {
                Map<String, Object> resourcePayload = toResourcePayload(resource);
                if (resourcePayload != null) {
                    resources.add(resourcePayload);
                }
            }
            payload.put("resources", resources);
        }
        return payload;
    }

    private static Map<String, Object> toResourcePayload(Resource resource) {
        if (resource == null) {
            return null;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        putIfNotNull(payload, "name", resource.getName());
        putIfNotNull(payload, "displayName", resource.getDisplayName());
        putIfNotNull(payload, "kind", resource.getKind());
        putIfNotNull(payload, "definition", toDefinitionPayload(resource.getDefinition()));
        return payload;
    }

    private static Map<String, Object> toDefinitionPayload(ResourceDefinition definition) {
        if (definition == null) {
            return null;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        putIfNotNull(payload, "schemaVersion", definition.getSchemaVersion());
        putIfNotNull(payload, "version", definition.getVersion());
        putIfNotNull(payload, "config", sanitizeValue(definition.getConfig()));
        return payload;
    }

    private static Object sanitizeValue(Object value) {
        if (value instanceof Map<?, ?> mapValue) {
            Map<String, Object> sanitized = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                if (entry.getValue() != null) {
                    sanitized.put(String.valueOf(entry.getKey()), sanitizeValue(entry.getValue()));
                }
            }
            return sanitized;
        }

        if (value instanceof List<?> listValue) {
            List<Object> sanitized = new ArrayList<>(listValue.size());
            for (Object item : listValue) {
                sanitized.add(sanitizeValue(item));
            }
            return sanitized;
        }

        return value;
    }

    private static void putIfNotNull(Map<String, Object> payload,
                                     String key,
                                     Object value) {
        if (value != null) {
            payload.put(key, value);
        }
    }
}
