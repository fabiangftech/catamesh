package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

class DestroyRequestAdapterTest {

    @Test
    void requestedDefinitionVersionsByResourceDeduplicatesAndPreservesInsertionOrder() {
        List<Resource> requestedResources = List.of(
                resource("orders", "0.0.2"),
                resource("orders", "0.0.2"),
                resource("orders", "0.0.1"),
                resource("payments", "1.0.0")
        );

        Map<String, LinkedHashSet<String>> grouped = DestroyRequestAdapter.requestedDefinitionVersionsByResource(requestedResources);

        Assertions.assertEquals(List.of("orders", "payments"), new ArrayList<>(grouped.keySet()));
        Assertions.assertEquals(List.of("0.0.2", "0.0.1"), new ArrayList<>(grouped.get("orders")));
        Assertions.assertEquals(List.of("1.0.0"), new ArrayList<>(grouped.get("payments")));
    }

    private Resource resource(String name, String version) {
        return new Resource(
                name,
                name,
                ResourceKind.BUCKET,
                new ResourceDefinition(SchemaVersion.BUCKET_V1, version, Map.of("lifecycleDays", 30))
        );
    }
}
