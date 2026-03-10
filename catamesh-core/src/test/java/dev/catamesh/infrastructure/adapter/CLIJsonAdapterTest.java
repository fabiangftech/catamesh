package dev.catamesh.infrastructure.adapter;

import dev.catamesh.infrastructure.config.JSONConfig;
import dev.catamesh.infrastructure.dto.CLIErrorPayloadDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

class CLIJsonAdapterTest {

    private final ObjectMapper jsonMapper = new JSONConfig().jsonMapper();

    @Test
    void toJsonSerializesCliPayload() throws Exception {
        CLIErrorPayloadDTO payload = new CLIErrorPayloadDTO(
                "VALIDATION_ERROR",
                20,
                "Validation failed",
                "resource is invalid",
                null,
                List.of("$.resources[0].name is required")
        );

        String json = CLIJsonAdapter.toJson(payload, jsonMapper);
        JsonNode node = jsonMapper.readTree(json);

        Assertions.assertEquals("VALIDATION_ERROR", node.get("errorCode").asText());
        Assertions.assertEquals(20, node.get("status").asInt());
        Assertions.assertEquals("$.resources[0].name is required", node.get("details").get(0).asText());
    }
}
