package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.DataProductKind;
import dev.catamesh.core.model.Metadata;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import dev.catamesh.core.model.Spec;
import dev.catamesh.infrastructure.config.JSONConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

class SchemaPayloadAdapterTest {

    private final ObjectMapper jsonMapper = new JSONConfig().jsonMapper();

    @Test
    void toJsonSerializesDataProduct() throws Exception {
        DataProduct dataProduct = dataProduct();

        String json = SchemaPayloadAdapter.toJson(dataProduct, jsonMapper);
        JsonNode node = jsonMapper.readTree(json);

        Assertions.assertEquals("data-product/v1", node.get("schemaVersion").asText());
        Assertions.assertEquals("sales-dp", node.get("metadata").get("name").asText());
        Assertions.assertEquals("bucket", node.get("spec").get("resources").get(0).get("kind").asText());
    }

    @Test
    void toJsonSerializesResource() throws Exception {
        Resource resource = resource("orders", "Orders", definition("0.0.1"));

        String json = SchemaPayloadAdapter.toJson(resource, jsonMapper);
        JsonNode node = jsonMapper.readTree(json);

        Assertions.assertEquals("orders", node.get("name").asText());
        Assertions.assertEquals("bucket", node.get("kind").asText());
        Assertions.assertEquals("0.0.1", node.get("definition").get("version").asText());
    }

    @Test
    void toJsonSerializesResourceDefinition() throws Exception {
        ResourceDefinition definition = definition("0.0.2");

        String json = SchemaPayloadAdapter.toJson(definition, jsonMapper);
        JsonNode node = jsonMapper.readTree(json);

        Assertions.assertEquals("bucket/v1", node.get("schemaVersion").asText());
        Assertions.assertEquals("0.0.2", node.get("version").asText());
        Assertions.assertEquals(30, node.get("config").get("lifecycleDays").asInt());
    }

    private DataProduct dataProduct() {
        return new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                new Metadata("sales-dp", "Sales", "sales", "Sales data product"),
                new Spec(DataProductKind.SOURCE_ALIGNED, List.of(resource("orders", "Orders", definition("0.0.1"))))
        );
    }

    private Resource resource(String name, String displayName, ResourceDefinition definition) {
        return new Resource(name, displayName, ResourceKind.BUCKET, definition);
    }

    private ResourceDefinition definition(String version) {
        return new ResourceDefinition(
                SchemaVersion.BUCKET_V1,
                version,
                Map.of("lifecycleDays", 30)
        );
    }
}
