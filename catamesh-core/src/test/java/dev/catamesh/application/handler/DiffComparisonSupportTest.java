package dev.catamesh.application.handler;

import dev.catamesh.core.model.DiffChange;
import dev.catamesh.core.model.DiffOp;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

class DiffComparisonSupportTest {

    private final DiffComparisonSupport support = new DiffComparisonSupport();

    @Test
    void compareResourceReturnsAddPayloadForNewResource() {
        List<DiffChange> changes = support.compareResource(resource("orders", "Orders", definition("0.0.1", 30)), null);

        Assertions.assertEquals(1, changes.size());
        Assertions.assertEquals(DiffOp.ADD, changes.get(0).getOp());
        Assertions.assertEquals("resource", changes.get(0).getPath());
        Assertions.assertEquals("orders", castMap(changes.get(0).getDesired()).get("name"));
    }

    @Test
    void compareResourceReturnsRemovePayloadForDeletedResource() {
        List<DiffChange> changes = support.compareResource(null, resource("orders", "Orders", definition("0.0.1", 30)));

        Assertions.assertEquals(1, changes.size());
        Assertions.assertEquals(DiffOp.REMOVE, changes.get(0).getOp());
        Assertions.assertEquals("resource", changes.get(0).getPath());
        Assertions.assertEquals("orders", castMap(changes.get(0).getCurrent()).get("name"));
    }

    @Test
    void compareResourceReturnsReplaceChangesForUpdatedFields() {
        Resource current = resource("orders", "Orders", definition("0.0.1", 30));
        Resource desired = resource("orders", "Orders Updated", definition("0.0.1", 45));

        List<DiffChange> changes = support.sortByPath(support.compareResource(desired, current));

        Assertions.assertEquals(2, changes.size());
        Assertions.assertEquals("definition.config.lifecycleDays", changes.get(0).getPath());
        Assertions.assertEquals(DiffOp.REPLACE, changes.get(0).getOp());
        Assertions.assertEquals("displayName", changes.get(1).getPath());
        Assertions.assertEquals(DiffOp.REPLACE, changes.get(1).getOp());
    }

    @Test
    void compareResourceHandlesNullDefinitionAsRemovals() {
        Resource current = resource("orders", "Orders", definition("0.0.1", 30));
        Resource desired = resource("orders", "Orders", null);

        List<DiffChange> changes = support.compareResource(desired, current);

        Assertions.assertEquals(3, changes.size());
        Assertions.assertEquals(Set.of(
                "definition.schemaVersion",
                "definition.version",
                "definition.config"
        ), changes.stream().map(DiffChange::getPath).collect(java.util.stream.Collectors.toSet()));
        Assertions.assertTrue(changes.stream().allMatch(change -> change.getOp().equals(DiffOp.REMOVE)));
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

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) {
        return (Map<String, Object>) value;
    }
}
