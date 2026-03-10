package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.SchemaVersion;
import dev.catamesh.infrastructure.config.JSONConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

class ResourceDefinitionAdapterTest {

    private final ObjectMapper jsonMapper = new JSONConfig().jsonMapper();

    @Test
    void toConfigJsonSerializesDefinitionConfig() throws Exception {
        ResourceDefinition definition = new ResourceDefinition(
                SchemaVersion.BUCKET_V1,
                "0.0.3",
                Map.of("lifecycleDays", 45, "region", "cl")
        );

        String json = ResourceDefinitionAdapter.toConfigJson(definition, jsonMapper);
        JsonNode node = jsonMapper.readTree(json);

        Assertions.assertEquals(45, node.get("lifecycleDays").asInt());
        Assertions.assertEquals("cl", node.get("region").asText());
    }
}
