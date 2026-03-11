package dev.catamesh.core.model.v2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class DiffEngineTest {

    @Test
    void compareHandlesNullObjectBranchesListsMapsAndValues() {
        SampleNode desired = new SampleNode(
                "updated",
                List.of("alpha", "beta"),
                Map.of("threshold", 10, "mode", "strict"),
                new SampleChild("child-value"),
                null
        );
        SampleNode current = new SampleNode(
                "current",
                List.of("alpha"),
                Map.of("threshold", 5),
                null,
                null
        );

        DiffResult result = DiffEngine.builder().build().compare(desired, current);
        DiffTreeNode root = result.getRoot();

        Assertions.assertEquals(DiffNodeKind.OBJECT, root.getKind());
        Assertions.assertTrue(result.hasChanges());
        Assertions.assertEquals(DiffChangeType.UPDATE, root.getFields().get("name").getChangeType());
        Assertions.assertEquals(DiffNodeKind.LIST, root.getFields().get("tags").getKind());
        Assertions.assertEquals(DiffChangeType.NONE, root.getFields().get("nullableChild").getChangeType());
        Assertions.assertEquals(DiffNodeKind.NULL, root.getFields().get("nullableChild").getKind());

        DiffTreeNode childNode = root.getFields().get("child");
        Assertions.assertEquals(DiffNodeKind.OBJECT, childNode.getKind());
        Assertions.assertEquals(DiffChangeType.CREATE, childNode.getFields().get("value").getChangeType());

        DiffTreeNode tagsNode = root.getFields().get("tags");
        Assertions.assertEquals(2, tagsNode.getElements().size());
        Assertions.assertEquals(DiffChangeType.NONE, tagsNode.getElements().get(0).getChangeType());
        Assertions.assertEquals(DiffChangeType.CREATE, tagsNode.getElements().get(1).getChangeType());

        DiffTreeNode attributesNode = root.getFields().get("attributes");
        Assertions.assertEquals(DiffNodeKind.MAP, attributesNode.getKind());
        Assertions.assertEquals(DiffChangeType.UPDATE, attributesNode.getEntries().get("threshold").getChangeType());
        Assertions.assertEquals(DiffChangeType.CREATE, attributesNode.getEntries().get("mode").getChangeType());
    }

    @Test
    void compareHandlesWholeObjectCreateAndDelete() {
        SampleNode node = new SampleNode(
                "created",
                List.of("alpha"),
                Map.of("threshold", 1),
                new SampleChild("child"),
                null
        );

        DiffResult createResult = DiffEngine.builder().build().compare(node, null);
        DiffResult deleteResult = DiffEngine.builder().build().compare(null, node);

        Assertions.assertTrue(createResult.hasChanges());
        Assertions.assertTrue(deleteResult.hasChanges());
        Assertions.assertEquals(DiffNodeKind.OBJECT, createResult.getRoot().getKind());
        Assertions.assertEquals(DiffNodeKind.OBJECT, deleteResult.getRoot().getKind());
        Assertions.assertEquals(DiffChangeType.CREATE, createResult.getRoot().getFields().get("name").getChangeType());
        Assertions.assertEquals(DiffChangeType.DELETE, deleteResult.getRoot().getFields().get("name").getChangeType());
    }

    private record SampleNode(
            String name,
            List<String> tags,
            Map<String, Object> attributes,
            SampleChild child,
            SampleChild nullableChild
    ) {
    }

    private record SampleChild(String value) {
    }
}
