package dev.catamesh.application.strategy;

import dev.catamesh.core.model.DiffChange;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.strategy.DiffStrategy;
import dev.catamesh.infrastructure.adapter.DiffPayloadAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public class ResourceDiffStrategy implements DiffStrategy<Resource> {

    private static final String RESOURCE_PATH = "resource";

    @Override
    public List<DiffChange> compare(Resource desired, Resource current, String path) {
        if (desired != null && current == null) {
            return List.of(DiffChange.add(resourcePath(path), DiffPayloadAdapter.toResourcePayload(desired)));
        }
        if (desired == null && current != null) {
            return List.of(DiffChange.remove(resourcePath(path), DiffPayloadAdapter.toResourcePayload(current)));
        }
        if (desired == null) {
            return Collections.emptyList();
        }

        List<DiffChange> changes = new ArrayList<>();
        compareField(
                changes,
                DiffStrategySupport.path(path, "displayName"),
                desired.getDisplayName(),
                desired.getDisplayName() != null,
                current.getDisplayName(),
                current.getDisplayName() != null
        );
        compareField(
                changes,
                DiffStrategySupport.path(path, "kind"),
                desired.getKind(),
                desired.getKind() != null,
                current.getKind(),
                current.getKind() != null
        );
        compareDefinitionField(
                changes,
                path,
                "schemaVersion",
                desired.getDefinition() == null ? null : desired.getDefinition().getSchemaVersion(),
                desired.getDefinition() != null && desired.getDefinition().getSchemaVersion() != null,
                current.getDefinition() == null ? null : current.getDefinition().getSchemaVersion(),
                current.getDefinition() != null && current.getDefinition().getSchemaVersion() != null
        );
        compareDefinitionField(
                changes,
                path,
                "version",
                desired.getDefinition() == null ? null : desired.getDefinition().getVersion(),
                desired.getDefinition() != null && desired.getDefinition().getVersion() != null,
                current.getDefinition() == null ? null : current.getDefinition().getVersion(),
                current.getDefinition() != null && current.getDefinition().getVersion() != null
        );
        compareDefinitionField(
                changes,
                path,
                "config",
                desired.getDefinition() == null ? null : desired.getDefinition().getConfig(),
                desired.getDefinition() != null && desired.getDefinition().getConfig() != null,
                current.getDefinition() == null ? null : current.getDefinition().getConfig(),
                current.getDefinition() != null && current.getDefinition().getConfig() != null
        );
        return DiffStrategySupport.sortByPath(changes);
    }

    private void compareDefinitionField(List<DiffChange> changes,
                                        String path,
                                        String field,
                                        Object desired,
                                        boolean hasDesired,
                                        Object current,
                                        boolean hasCurrent) {
        compareField(
                changes,
                DiffStrategySupport.path(path, "definition." + field),
                desired,
                hasDesired,
                current,
                hasCurrent
        );
    }

    private void compareField(List<DiffChange> changes,
                              String path,
                              Object desired,
                              boolean hasDesired,
                              Object current,
                              boolean hasCurrent) {
        DiffStrategySupport.compareValue(path, desired, hasDesired, current, hasCurrent, changes);
    }

    private String resourcePath(String path) {
        return DiffStrategySupport.path(path, RESOURCE_PATH);
    }
}
