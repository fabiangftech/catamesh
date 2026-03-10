package dev.catamesh.application.strategy;

import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.DiffChange;
import dev.catamesh.core.strategy.DiffStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataProductDiffStrategy implements DiffStrategy<DataProduct> {

    @Override
    public List<DiffChange> compare(DataProduct desired, DataProduct current, String path) {
        if (desired == null) {
            return Collections.emptyList();
        }

        List<DiffChange> changes = new ArrayList<>();
        compareField(
                changes,
                DiffStrategySupport.path(path, "schemaVersion"),
                desired.getSchemaVersion(),
                desired.getSchemaVersion() != null,
                current == null ? null : current.getSchemaVersion(),
                current != null && current.getSchemaVersion() != null
        );
        compareField(
                changes,
                DiffStrategySupport.path(path, "metadata.displayName"),
                desired.getMetadata().getDisplayName(),
                desired.getMetadata().getDisplayName() != null,
                current == null ? null : current.getMetadata().getDisplayName(),
                current != null && current.getMetadata().getDisplayName() != null
        );
        compareField(
                changes,
                DiffStrategySupport.path(path, "metadata.domain"),
                desired.getMetadata().getDomain(),
                desired.getMetadata().getDomain() != null,
                current == null ? null : current.getMetadata().getDomain(),
                current != null && current.getMetadata().getDomain() != null
        );
        compareField(
                changes,
                DiffStrategySupport.path(path, "metadata.description"),
                desired.getMetadata().getDescription(),
                desired.getMetadata().getDescription() != null,
                current == null ? null : current.getMetadata().getDescription(),
                current != null && current.getMetadata().getDescription() != null
        );
        compareField(
                changes,
                DiffStrategySupport.path(path, "spec.kind"),
                desired.getSpec().getKind(),
                desired.getSpec().getKind() != null,
                current == null ? null : current.getSpec().getKind(),
                current != null && current.getSpec().getKind() != null
        );
        return DiffStrategySupport.sortByPath(changes);
    }

    private void compareField(List<DiffChange> changes,
                              String path,
                              Object desired,
                              boolean hasDesired,
                              Object current,
                              boolean hasCurrent) {
        DiffStrategySupport.compareValue(path, desired, hasDesired, current, hasCurrent, changes);
    }
}
