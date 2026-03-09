package dev.catamesh.application.handler;


import dev.catamesh.core.model.*;

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
            return List.of(DiffChange.add("resource", toResourcePayload(desiredResource)));
        }
        if (desiredResource == null && currentResource != null) {
            return List.of(DiffChange.remove("resource", toResourcePayload(currentResource)));
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
        if (resources == null || resources.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Resource> byName = new HashMap<>();
        for (Resource resource : resources) {
            byName.put(resource.getName(), resource);
        }
        return byName;
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
            changes.add(DiffChange.add(path, normalize(desired)));
            return;
        }
        if (!hasDesired) {
            changes.add(DiffChange.remove(path, normalize(current)));
            return;
        }

        if (desired instanceof Map<?, ?> desiredMap && current instanceof Map<?, ?> currentMap) {
            compareMap(path, desiredMap, currentMap, changes);
            return;
        }

        if (desired instanceof List<?> desiredList && current instanceof List<?> currentList) {
            if (!Objects.equals(normalize(desiredList), normalize(currentList))) {
                changes.add(DiffChange.replace(path, normalize(currentList), normalize(desiredList)));
            }
            return;
        }

        Object normalizedDesired = normalize(desired);
        Object normalizedCurrent = normalize(current);
        if (!Objects.equals(normalizedDesired, normalizedCurrent)) {
            changes.add(DiffChange.replace(path, normalizedCurrent, normalizedDesired));
        }
    }

    private void compareMap(String path,
                            Map<?, ?> desiredMap,
                            Map<?, ?> currentMap,
                            List<DiffChange> changes) {
        Map<String, Object> desiredByKey = toStringKeyedMap(desiredMap);
        Map<String, Object> currentByKey = toStringKeyedMap(currentMap);
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

    private Map<String, Object> toStringKeyedMap(Map<?, ?> map) {
        Map<String, Object> stringKeyedMap = new HashMap<>();
        map.forEach((key, value) -> stringKeyedMap.put(key.toString(), value));
        return stringKeyedMap;
    }

    private Map<String, Object> toResourcePayload(Resource resource) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("name", resource.getName());
        payload.put("displayName", resource.getDisplayName());
        payload.put("kind", normalize(resource.getKind()));
        payload.put("definition", toResourceDefinitionPayload(resource.getDefinition()));
        return payload;
    }

    private Map<String, Object> toResourceDefinitionPayload(ResourceDefinition definition) {
        if (definition == null) {
            return new HashMap<>();
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("schemaVersion", normalize(definition.getSchemaVersion()));
        payload.put("version", definition.getVersion());
        payload.put("config", normalize(definition.getConfig()));
        return payload;
    }

    private Object normalize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof SchemaVersion schemaVersion) {
            return schemaVersion.getValue();
        }
        if (value instanceof DataProductKind dataProductKind) {
            return dataProductKind.getValue();
        }
        if (value instanceof ResourceKind resourceKind) {
            return resourceKind.getValue();
        }
        if (value instanceof Key key) {
            return key.value();
        }
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> normalized = new LinkedHashMap<>();
            map.keySet().stream()
                    .map(Object::toString)
                    .sorted()
                    .forEach(key -> normalized.put(key, normalize(map.get(key))));
            return normalized;
        }
        if (value instanceof List<?> list) {
            return list.stream()
                    .map(this::normalize)
                    .toList();
        }
        return value;
    }
}
