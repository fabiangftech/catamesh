package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import tools.jackson.databind.ObjectMapper;

public final class SchemaPayloadAdapter {

    private SchemaPayloadAdapter() {
        // utility class
    }

    public static String toJson(DataProduct dataProduct, ObjectMapper jsonMapper) {
        return jsonMapper.writeValueAsString(dataProduct);
    }

    public static String toJson(Resource resource, ObjectMapper jsonMapper) {
        return jsonMapper.writeValueAsString(resource);
    }

    public static String toJson(ResourceDefinition resourceDefinition, ObjectMapper jsonMapper) {
        return jsonMapper.writeValueAsString(resourceDefinition);
    }
}
