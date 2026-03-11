package dev.catamesh;

import dev.catamesh.core.model.*;
import dev.catamesh.core.model.v2.DiffEngine;
import dev.catamesh.core.model.v2.DiffResult;
import dev.catamesh.infrastructure.config.JSONConfig;
import org.junit.jupiter.api.Test;

import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Metadata;
import dev.catamesh.core.model.SchemaVersion;
import dev.catamesh.core.model.v2.*;
import java.util.ArrayList;
import java.util.List;

public class DiffTest {

    @Test
    void testDiff() {

        DataProduct dataProduct = new DataProduct();
        Metadata metadata = new Metadata("hello", "test", null, null);
        List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(Key.newId(), null, "hi", null, null));
        Spec spec = new Spec(null, resources);
        DataProduct dataProductTwo = new DataProduct(SchemaVersion.DATA_PRODUCT_V1, metadata, spec);

        DiffEngine diffEngine = DiffEngine.builder()
                .exclude("meta.id")
                .exclude("spec.resources[?].id")
                .exclude("spec.resources[?].name")
                .build();
        DiffResult diff = diffEngine.compare(dataProductTwo, dataProduct);

        System.out.println(new JSONConfig().jsonMapper().writeValueAsString(diff));
        System.out.println(diff.getSummary());

        printNode(diff.getRoot(), 0);
    }

    private void printNode(DiffTreeNode node, int level) {

        String indent = "  ".repeat(level);

        System.out.println(
                indent +
                node.getPath() +
                " [" + node.getKind() + "] " +
                node.getChangeType() +
                " old=" + node.getOldValue() +
                " new=" + node.getNewValue()
        );

        // OBJECT fields
        for (DiffTreeNode child : node.getFields().values()) {
            printNode(child, level + 1);
        }

        // LIST elements
        for (DiffTreeNode child : node.getElements()) {
            printNode(child, level + 1);
        }

        // MAP entries
        for (DiffTreeNode child : node.getEntries().values()) {
            printNode(child, level + 1);
        }
    }
}
