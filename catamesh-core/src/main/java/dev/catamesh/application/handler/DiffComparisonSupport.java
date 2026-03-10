package dev.catamesh.application.handler;


import dev.catamesh.core.model.*;

import dev.catamesh.infrastructure.adapter.DiffPayloadAdapter;

import java.util.*;

public class DiffComparisonSupport {

    public List<DiffChange> compareDataProduct(DataProduct desired, DataProduct current) {
        List<DiffChange> changes = new ArrayList<>();
        compareValue(
                "schemaVersion",
                desired.getSchemaVersion(),
                desired.getSchemaVersion() != null,
                current == null ? null : current.getSchemaVersion(),
                current != null && current.getSchemaVersion() != null,
                changes
        );
        compareValue(
                "metadata.displayName",
                desired.getMetadata().getDisplayName(),
                desired.getMetadata().getDisplayName() != null,
                current == null ? null : current.getMetadata().getDisplayName(),
                current != null && current.getMetadata().getDisplayName() != null,
                changes
        );
        compareValue(
                "metadata.domain",
                desired.getMetadata().getDomain(),
                desired.getMetadata().getDomain() != null,
                current == null ? null : current.getMetadata().getDomain(),
                current != null && current.getMetadata().getDomain() != null,
                changes
        );
        compareValue(
                "metadata.description",
                desired.getMetadata().getDescription(),
                desired.getMetadata().getDescription() != null,
                current == null ? null : current.getMetadata().getDescription(),
                current != null && current.getMetadata().getDescription() != null,
                changes
        );
        compareValue(
                "spec.kind",
                desired.getSpec().getKind(),
                desired.getSpec().getKind() != null,
                current == null ? null : current.getSpec().getKind(),
                current != null && current.getSpec().getKind() != null,
                changes
        );
        return sortByPath(changes);
    }

    public List<DiffChange> compareResource(Resource desiredResource, Resource currentResource) {
        if (desiredResource != null && currentResource == null) {
            return List.of(DiffChange.add("resource", DiffPayloadAdapter.toResourcePayload(desiredResource)));
        }
        if (desiredResource == null && currentResource != null) {
            return List.of(DiffChange.remove("resource", DiffPayloadAdapter.toResourcePayload(currentResource)));
        }
        if (desiredResource == null) {
            return Collections.emptyList();
        }

        List<DiffChange> changes = new ArrayList<>();
        compareValue(
                "displayName",
                desiredResource.getDisplayName(),
                desiredResource.getDisplayName() != null,
                currentResource.getDisplayName(),
                currentResource.getDisplayName() != null,
                changes
        );
        compareValue(
                "kind",
                desiredResource.getKind(),
                desiredResource.getKind() != null,
                currentResource.getKind(),
                currentResource.getKind() != null,
                changes
        );
        compareValue(
                "definition.schemaVersion",
                desiredResource.getDefinition() == null ? null : desiredResource.getDefinition().getSchemaVersion(),
                desiredResource.getDefinition() != null
                        && desiredResource.getDefinition().getSchemaVersion() != null,
                currentResource.getDefinition() == null ? null : currentResource.getDefinition().getSchemaVersion(),
                currentResource.getDefinition() != null
                        && currentResource.getDefinition().getSchemaVersion() != null,
                changes
        );
        compareValue(
                "definition.version",
                desiredResource.getDefinition() == null ? null : desiredResource.getDefinition().getVersion(),
                desiredResource.getDefinition() != null
                        && desiredResource.getDefinition().getVersion() != null,
                currentResource.getDefinition() == null ? null : currentResource.getDefinition().getVersion(),
                currentResource.getDefinition() != null
                        && currentResource.getDefinition().getVersion() != null,
                changes
        );
        compareValue(
                "definition.config",
                desiredResource.getDefinition() == null ? null : desiredResource.getDefinition().getConfig(),
                desiredResource.getDefinition() != null
                        && desiredResource.getDefinition().getConfig() != null,
                currentResource.getDefinition() == null ? null : currentResource.getDefinition().getConfig(),
                currentResource.getDefinition() != null
                        && currentResource.getDefinition().getConfig() != null,
                changes
        );
        return changes;
    }

    public Map<String, Resource> byResourceName(List<Resource> resources) {
        return DiffPayloadAdapter.byResourceName(resources);
    }

    public List<DiffChange> sortByPath(List<DiffChange> changes) {
        return changes.stream()
                .sorted(Comparator.comparing(DiffChange::getPath))
                .toList();
    }

    private void compareValue(String path,
                              Object desired,
                              boolean hasDesired,
                              Object current,
                              boolean hasCurrent,
                              List<DiffChange> changes) {
        if (!hasDesired && !hasCurrent) {
            return;
        }
        if (hasDesired && !hasCurrent) {
            changes.add(DiffChange.add(path, DiffPayloadAdapter.normalize(desired)));
            return;
        }
        if (!hasDesired) {
            changes.add(DiffChange.remove(path, DiffPayloadAdapter.normalize(current)));
            return;
        }

        if (desired instanceof Map<?, ?> desiredMap && current instanceof Map<?, ?> currentMap) {
            compareMap(path, desiredMap, currentMap, changes);
            return;
        }

        if (desired instanceof List<?> desiredList && current instanceof List<?> currentList) {
            if (!Objects.equals(DiffPayloadAdapter.normalize(desiredList), DiffPayloadAdapter.normalize(currentList))) {
                changes.add(DiffChange.replace(
                        path,
                        DiffPayloadAdapter.normalize(currentList),
                        DiffPayloadAdapter.normalize(desiredList)
                ));
            }
            return;
        }

        Object normalizedDesired = DiffPayloadAdapter.normalize(desired);
        Object normalizedCurrent = DiffPayloadAdapter.normalize(current);
        if (!Objects.equals(normalizedDesired, normalizedCurrent)) {
            changes.add(DiffChange.replace(path, normalizedCurrent, normalizedDesired));
        }
    }

    private void compareMap(String path,
                            Map<?, ?> desiredMap,
                            Map<?, ?> currentMap,
                            List<DiffChange> changes) {
        Map<String, Object> desiredByKey = DiffPayloadAdapter.toStringKeyedMap(desiredMap);
        Map<String, Object> currentByKey = DiffPayloadAdapter.toStringKeyedMap(currentMap);
        SortedSet<String> keys = new TreeSet<>();
        keys.addAll(desiredByKey.keySet());
        keys.addAll(currentByKey.keySet());

        for (String key : keys) {
            boolean hasDesired = desiredByKey.containsKey(key);
            boolean hasCurrent = currentByKey.containsKey(key);
            compareValue(
                    path + "." + key,
                    hasDesired ? desiredByKey.get(key) : null,
                    hasDesired,
                    hasCurrent ? currentByKey.get(key) : null,
                    hasCurrent,
                    changes
            );
        }
    }
}
