package dev.catamesh.application.strategy;

import dev.catamesh.core.model.DiffChange;
import dev.catamesh.core.model.DiffOp;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class ResourceDiffOLDStrategyTest {

    private final ResourceDiffOLDStrategy strategy = new ResourceDiffOLDStrategy();

    @Test
    void compareReturnsAddPayloadForNewResource() {
        List<DiffChange> changes = strategy.compare(resource("orders", "Orders", definition("0.0.1", 30)), null, "");

        Assertions.assertEquals(1, changes.size());
        Assertions.assertEquals(DiffOp.ADD, changes.get(0).getOp());
        Assertions.assertEquals("resource", changes.get(0).getPath());
        Assertions.assertEquals("orders", castMap(changes.get(0).getDesired()).get("name"));
    }

    @Test
    void compareReturnsRemovePayloadForDeletedResource() {
        List<DiffChange> changes = strategy.compare(null, resource("orders", "Orders", definition("0.0.1", 30)), "");

        Assertions.assertEquals(1, changes.size());
        Assertions.assertEquals(DiffOp.REMOVE, changes.get(0).getOp());
        Assertions.assertEquals("resource", changes.get(0).getPath());
        Assertions.assertEquals("orders", castMap(changes.get(0).getCurrent()).get("name"));
    }

    @Test
    void compareReturnsSortedReplaceChangesForUpdatedFields() {
        Resource current = resource("orders", "Orders", definition("0.0.1", 30));
        Resource desired = resource("orders", "Orders Updated", definition("0.0.1", 45));

        List<DiffChange> changes = strategy.compare(desired, current, "");

        Assertions.assertEquals(List.of("definition.config.lifecycleDays", "displayName"),
                changes.stream().map(DiffChange::getPath).toList());
        Assertions.assertTrue(changes.stream().allMatch(change -> change.getOp().equals(DiffOp.REPLACE)));
    }

    @Test
    void compareHandlesNullDefinitionAsRemovals() {
        Resource current = resource("orders", "Orders", definition("0.0.1", 30));
        Resource desired = resource("orders", "Orders", null);

        List<DiffChange> changes = strategy.compare(desired, current, "");

        Assertions.assertEquals(Set.of(
                "definition.schemaVersion",
                "definition.version",
                "definition.config"
        ), changes.stream().map(DiffChange::getPath).collect(java.util.stream.Collectors.toSet()));
        Assertions.assertTrue(changes.stream().allMatch(change -> change.getOp().equals(DiffOp.REMOVE)));
    }

    @Test
    void compareIgnoresConfigMapOrderingWhenContentIsEquivalent() {
        Resource current = resource("orders", "Orders", new ResourceDefinition(
                SchemaVersion.BUCKET_V1,
                "0.0.1",
                linkedConfig("cl", 30)
        ));
        Resource desired = resource("orders", "Orders", new ResourceDefinition(
                SchemaVersion.BUCKET_V1,
                "0.0.1",
                Map.of(
                        "settings", List.of(Map.of("region", "cl", "lifecycleDays", 30))
                )
        ));

        List<DiffChange> changes = strategy.compare(desired, current, "");

        Assertions.assertTrue(changes.isEmpty());
    }

    private Resource resource(String name, String displayName, ResourceDefinition definition) {
        return new Resource(name, displayName, ResourceKind.BUCKET, definition);
    }

    private ResourceDefinition definition(String version, int lifecycleDays) {
        return new ResourceDefinition(
                SchemaVersion.BUCKET_V1,
                version,
                Map.of("lifecycleDays", lifecycleDays)
        );
    }

    private Map<String, Object> linkedConfig(String region, int lifecycleDays) {
        Map<String, Object> nested = new LinkedHashMap<>();
        nested.put("lifecycleDays", lifecycleDays);
        nested.put("region", region);

        Map<String, Object> config = new LinkedHashMap<>();
        config.put("settings", List.of(nested));
        return config;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) {
        return (Map<String, Object>) value;
    }
}
