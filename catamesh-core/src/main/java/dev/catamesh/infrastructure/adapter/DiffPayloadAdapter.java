package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.DataProductKind;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public final class DiffPayloadAdapter {

    private DiffPayloadAdapter() {
        // utility class
    }

    public static Map<String, Resource> byResourceName(List<Resource> resources) {
        if (resources == null || resources.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Resource> byName = new HashMap<>();
        for (Resource resource : resources) {
            byName.put(resource.getName(), resource);
        }
        return byName;
    }

    public static Map<String, Object> toDataProductPayload(DataProduct dataProduct) {
        if (dataProduct == null) {
            return null;
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("schemaVersion", normalize(dataProduct.getSchemaVersion()));
        payload.put("metadata", toMetadataPayload(dataProduct));
        payload.put("spec", toSpecPayload(dataProduct));
        return payload;
    }

    public static Map<String, Object> toStringKeyedMap(Map<?, ?> map) {
        Map<String, Object> stringKeyedMap = new HashMap<>();
        map.forEach((key, value) -> stringKeyedMap.put(key.toString(), value));
        return stringKeyedMap;
    }

    public static Map<String, Object> toResourcePayload(Resource resource) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", resource.getName());
        payload.put("displayName", resource.getDisplayName());
        payload.put("kind", normalize(resource.getKind()));
        payload.put("definition", toResourceDefinitionPayload(resource.getDefinition()));
        return payload;
    }

    public static Map<String, Object> toResourcesPayload(List<Resource> resources) {
        if (resources == null || resources.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        new TreeMap<>(byResourceName(resources)).forEach((name, resource) -> payload.put(name, toResourcePayload(resource)));
        return payload;
    }

    public static Map<String, Object> toResourceDefinitionPayload(ResourceDefinition definition) {
        if (definition == null) {
            return new HashMap<>();
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("schemaVersion", normalize(definition.getSchemaVersion()));
        payload.put("version", definition.getVersion());
        payload.put("config", normalize(definition.getConfig()));
        return payload;
    }

    public static Object normalize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof SchemaVersion schemaVersion) {
            return schemaVersion.getValue();
        }
        if (value instanceof DataProductKind dataProductKind) {
            return dataProductKind.getValue();
        }
        if (value instanceof ResourceKind resourceKind) {
            return resourceKind.getValue();
        }
        if (value instanceof Key key) {
            return key.value();
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> normalized = new LinkedHashMap<>();
            map.keySet().stream()
                    .map(Object::toString)
                    .sorted()
                    .forEach(key -> normalized.put(key, normalize(map.get(key))));
            return normalized;
        }
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(DiffPayloadAdapter::normalize)
                    .toList();
        }
        return value;
    }

    private static Map<String, Object> toMetadataPayload(DataProduct dataProduct) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", dataProduct.getMetadata().getName());
        payload.put("displayName", dataProduct.getMetadata().getDisplayName());
        payload.put("domain", dataProduct.getMetadata().getDomain());
        payload.put("description", dataProduct.getMetadata().getDescription());
        return payload;
    }

    private static Map<String, Object> toSpecPayload(DataProduct dataProduct) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("kind", normalize(dataProduct.getSpec().getKind()));
        payload.put("resources", toResourcesPayload(dataProduct.getSpec().getResources()));
        return payload;
    }
}
