package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.DataProductKind;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class DiffPayloadAdapterTest {

    @Test
    void byResourceNameIndexesResourcesByName() {
        Resource orders = resource("orders", definition("0.0.1", 30));
        Resource customers = resource("customers", definition("0.0.1", 7));

        Map<String, Resource> resourcesByName = DiffPayloadAdapter.byResourceName(List.of(orders, customers));

        Assertions.assertEquals(orders, resourcesByName.get("orders"));
        Assertions.assertEquals(customers, resourcesByName.get("customers"));
    }

    @Test
    void normalizeConvertsEnumsKeysAndListsToStablePayloads() {
        Map<String, Object> normalized = castMap(DiffPayloadAdapter.normalize(Map.of(
                "zeta", Key.create("key-1"),
                "alpha", List.of(SchemaVersion.BUCKET_V1, DataProductKind.SOURCE_ALIGNED, ResourceKind.BUCKET)
        )));

        Assertions.assertEquals(List.of("alpha", "zeta"), new ArrayList<>(normalized.keySet()));
        Assertions.assertEquals(List.of("bucket/v1", "source-aligned", "bucket"), normalized.get("alpha"));
        Assertions.assertEquals("key-1", normalized.get("zeta"));
    }

    @Test
    void toResourcePayloadIncludesNormalizedDefinition() {
        Map<String, Object> payload = DiffPayloadAdapter.toResourcePayload(resource("orders", definition("0.0.2", 45)));
        Map<String, Object> definitionPayload = castMap(payload.get("definition"));

        Assertions.assertEquals("orders", payload.get("name"));
        Assertions.assertEquals("bucket", payload.get("kind"));
        Assertions.assertEquals("bucket/v1", definitionPayload.get("schemaVersion"));
        Assertions.assertEquals("0.0.2", definitionPayload.get("version"));
        Assertions.assertEquals(45, castMap(definitionPayload.get("config")).get("lifecycleDays"));
    }

    private Resource resource(String name, ResourceDefinition definition) {
        return new Resource(name, name, ResourceKind.BUCKET, definition);
    }

    private ResourceDefinition definition(String version, int lifecycleDays) {
        return new ResourceDefinition(
                SchemaVersion.BUCKET_V1,
                version,
                new LinkedHashMap<>(Map.of("lifecycleDays", lifecycleDays))
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) {
        return (Map<String, Object>) value;
    }
}
