package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.DataProductKind;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Metadata;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.model.ResourceKind;
import dev.catamesh.core.model.SchemaVersion;
import dev.catamesh.core.model.Spec;
import dev.catamesh.core.model.DiffChangeType;
import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.DiffTreeNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class BuildDiffResultHandlerTest {

    private final BuildDiffResultHandler handler = new BuildDiffResultHandler();

    @Test
    void handleBuildsNoopDiffWhenOnlyIdsAndResourceOrderDiffer() {
        ApplyDataProductContext context = ApplyDataProductContext.create("yaml");
        context.setDataProduct(dataProduct(
                "sales",
                "Sales",
                "finance",
                "Finance data product",
                DataProductKind.SOURCE_ALIGNED,
                List.of(
                        resource("orders", "Orders", "0.0.1", 30, "resource-orders-new", "dp-new"),
                        resource("customers", "Customers", "0.0.1", 7, "resource-customers-new", "dp-new")
                )
        ));
        context.setCurrentDataProduct(dataProduct(
                "sales",
                "Sales",
                "finance",
                "Finance data product",
                DataProductKind.SOURCE_ALIGNED,
                List.of(
                        resource("customers", "Customers", "0.0.1", 7, "resource-customers-old", "dp-old"),
                        resource("orders", "Orders", "0.0.1", 30, "resource-orders-old", "dp-old")
                )
        ));

        handler.handle(context);

        DiffResult diff = context.getDiffResult();
        Assertions.assertFalse(diff.hasChanges());
        Assertions.assertEquals(0, diff.getSummary().getAdded());
        Assertions.assertEquals(0, diff.getSummary().getChanged());
        Assertions.assertEquals(0, diff.getSummary().getRemoved());
    }

    @Test
    void handleReportsMetadataKindAndResourceChanges() {
        ApplyDataProductContext context = ApplyDataProductContext.create("yaml");
        context.setDataProduct(dataProduct(
                "sales",
                "Sales Updated",
                "finance",
                "Desired description",
                DataProductKind.SOURCE_ALIGNED,
                List.of(
                        resource("orders", "Orders", "0.0.1", 45, "resource-orders-new", "dp-new"),
                        resource("customers", "Customers", "0.0.2", 7, "resource-customers-new", "dp-new")
                )
        ));
        context.setCurrentDataProduct(dataProduct(
                "sales",
                "Sales",
                "finance",
                "Current description",
                null,
                List.of(
                        resource("orders", "Orders", "0.0.1", 30, "resource-orders-old", "dp-old"),
                        resource("legacy", "Legacy", "0.0.1", 14, "resource-legacy-old", "dp-old")
                )
        ));

        handler.handle(context);

        DiffResult diff = context.getDiffResult();
        DiffTreeNode root = diff.getRoot();
        DiffTreeNode resourcesNode = root.getEntries().get("spec").getEntries().get("resources");

        Assertions.assertTrue(diff.hasChanges());
        Assertions.assertEquals(7, diff.getSummary().getAdded());
        Assertions.assertEquals(3, diff.getSummary().getChanged());
        Assertions.assertEquals(6, diff.getSummary().getRemoved());
        Assertions.assertEquals(
                DiffChangeType.UPDATE,
                root.getEntries().get("metadata").getEntries().get("displayName").getChangeType()
        );
        Assertions.assertEquals(
                DiffChangeType.UPDATE,
                root.getEntries().get("metadata").getEntries().get("description").getChangeType()
        );
        Assertions.assertEquals(
                DiffChangeType.CREATE,
                root.getEntries().get("spec").getEntries().get("kind").getChangeType()
        );
        Assertions.assertEquals(
                DiffChangeType.UPDATE,
                resourcesNode.getEntries().get("orders")
                        .getEntries().get("definition")
                        .getEntries().get("config")
                        .getEntries().get("lifecycleDays")
                        .getChangeType()
        );
        Assertions.assertEquals(
                DiffChangeType.CREATE,
                resourcesNode.getEntries().get("customers").getEntries().get("kind").getChangeType()
        );
        Assertions.assertEquals(
                DiffChangeType.DELETE,
                resourcesNode.getEntries().get("legacy").getEntries().get("kind").getChangeType()
        );
    }

    @Test
    void handleCreatesWholeTreeWhenCurrentIsMissing() {
        ApplyDataProductContext context = ApplyDataProductContext.create("yaml");
        context.setDataProduct(dataProduct(
                "sales",
                "Sales",
                "finance",
                "Finance data product",
                DataProductKind.SOURCE_ALIGNED,
                List.of(resource("orders", "Orders", "0.0.1", 30, "resource-orders-new", "dp-new"))
        ));
        context.setCurrentDataProduct(null);

        handler.handle(context);

        DiffResult diff = context.getDiffResult();
        DiffTreeNode root = diff.getRoot();

        Assertions.assertTrue(diff.hasChanges());
        Assertions.assertEquals(DiffChangeType.CREATE, root.getEntries().get("schemaVersion").getChangeType());
        Assertions.assertEquals(DiffChangeType.CREATE, root.getEntries().get("metadata").getEntries().get("name").getChangeType());
        Assertions.assertEquals(
                DiffChangeType.CREATE,
                root.getEntries().get("spec")
                        .getEntries().get("resources")
                        .getEntries().get("orders")
                        .getEntries().get("kind")
                        .getChangeType()
        );
    }

    private DataProduct dataProduct(String name,
                                    String displayName,
                                    String domain,
                                    String description,
                                    DataProductKind kind,
                                    List<Resource> resources) {
        return new DataProduct(
                SchemaVersion.DATA_PRODUCT_V1,
                new Metadata(Key.create("dp-" + name), name, displayName, domain, description),
                new Spec(kind, resources)
        );
    }

    private Resource resource(String name,
                              String displayName,
                              String version,
                              int lifecycleDays,
                              String id,
                              String dataProductId) {
        Resource resource = new Resource(name, displayName, ResourceKind.BUCKET, new ResourceDefinition(
                SchemaVersion.BUCKET_V1,
                version,
                Map.of("lifecycleDays", lifecycleDays)
        ));
        resource.setId(Key.create(id));
        resource.setDataProductId(Key.create(dataProductId));
        return resource;
    }
}
