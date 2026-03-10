package dev.catamesh.application.strategy;

import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.DataProductKind;
import dev.catamesh.core.model.DiffChange;
import dev.catamesh.core.model.DiffOp;
import dev.catamesh.core.model.Metadata;
import dev.catamesh.core.model.SchemaVersion;
import dev.catamesh.core.model.Spec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class DataProductDiffStrategyTest {

    private final DataProductDiffStrategy strategy = new DataProductDiffStrategy();

    @Test
    void compareReturnsSortedAddChangesWhenCurrentIsNull() {
        DataProduct desired = dataProduct("Sales", "sales", "Sales data product", SchemaVersion.DATA_PRODUCT_V1);

        List<DiffChange> changes = strategy.compare(desired, null, "");

        Assertions.assertEquals(List.of(
                "metadata.description",
                "metadata.displayName",
                "metadata.domain",
                "schemaVersion",
                "spec.kind"
        ), changes.stream().map(DiffChange::getPath).toList());
        Assertions.assertTrue(changes.stream().allMatch(change -> change.getOp().equals(DiffOp.ADD)));
    }

    @Test
    void compareReturnsReplaceAndRemoveChangesForUpdatedDataProduct() {
        DataProduct current = dataProduct("Sales", "sales", "Current description", SchemaVersion.DATA_PRODUCT_V1);
        DataProduct desired = dataProduct("Sales Updated", null, "Desired description", SchemaVersion.DATA_PRODUCT_V1);

        List<DiffChange> changes = strategy.compare(desired, current, "");

        Assertions.assertEquals(List.of(
                "metadata.description",
                "metadata.displayName",
                "metadata.domain"
        ), changes.stream().map(DiffChange::getPath).toList());
        Assertions.assertEquals(DiffOp.REPLACE, changes.get(0).getOp());
        Assertions.assertEquals(DiffOp.REPLACE, changes.get(1).getOp());
        Assertions.assertEquals(DiffOp.REMOVE, changes.get(2).getOp());
    }

    private DataProduct dataProduct(String displayName,
                                    String domain,
                                    String description,
                                    SchemaVersion schemaVersion) {
        return new DataProduct(
                schemaVersion,
                new Metadata("sales-dp", displayName, domain, description),
                new Spec(DataProductKind.SOURCE_ALIGNED, List.of())
        );
    }
}
