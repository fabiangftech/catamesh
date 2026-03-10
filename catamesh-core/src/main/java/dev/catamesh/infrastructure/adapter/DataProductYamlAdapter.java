package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.DataProduct;
import tools.jackson.databind.ObjectMapper;

public final class DataProductYamlAdapter {

    private DataProductYamlAdapter() {
        // utility class
    }

    public static DataProduct toDataProduct(String yaml, ObjectMapper yamlMapper) {
        return yamlMapper.readValue(yaml, DataProduct.class);
    }
}
