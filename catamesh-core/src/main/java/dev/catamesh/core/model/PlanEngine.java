package dev.catamesh.core.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class PlanEngine {
    private static final String METADATA_PATH = "metadata";
    private static final String SPEC_PATH = "spec";
    private static final String RESOURCES_KEY = "resources";
    private static final String DEFINITION_KEY = "definition";

    public PlanEngine() {
    }

    public PlanResult plan(DataProduct desired, DataProduct current, DiffResult diff) {
        Objects.requireNonNull(diff, "diff is required");

        PlanResult result = new PlanResult();
        DiffTreeNode root = diff.getRoot();

        DiffTreeNode metadataNode = child(root, METADATA_PATH);
        appendStep(
                result,
                PlanStepType.METADATA,
                METADATA_PATH,
                metadataNode == null ? null : copyMap(metadataNode.getOldValue()),
                metadataNode == null ? null : copyMap(metadataNode.getNewValue()),
                hasChanges(metadataNode)
        );

        DiffTreeNode specNode = child(root, SPEC_PATH);
        appendStep(
                result,
                PlanStepType.SPEC,
                SPEC_PATH,
                filterMap(specNode == null ? null : specNode.getOldValue(), RESOURCES_KEY),
                filterMap(specNode == null ? null : specNode.getNewValue(), RESOURCES_KEY),
                hasChangesExcluding(specNode, Set.of(RESOURCES_KEY))
        );

        DiffTreeNode resourcesNode = child(specNode, RESOURCES_KEY);
        Map<String, Object> currentResources = nestedMap(specNode == null ? null : specNode.getOldValue(), RESOURCES_KEY);
        Map<String, Object> desiredResources = nestedMap(specNode == null ? null : specNode.getNewValue(), RESOURCES_KEY);
        TreeSet<String> resourceNames = new TreeSet<>();
        resourceNames.addAll(currentResources.keySet());
        resourceNames.addAll(desiredResources.keySet());

        for (String resourceName : resourceNames) {
            DiffTreeNode resourceNode = child(resourcesNode, resourceName);
            String resourcePath = SPEC_PATH + "." + RESOURCES_KEY + "." + resourceName;

            appendStep(
                    result,
                    PlanStepType.RESOURCE,
                    resourcePath,
                    filterMap(resourceNode == null ? null : resourceNode.getOldValue(), DEFINITION_KEY),
                    filterMap(resourceNode == null ? null : resourceNode.getNewValue(), DEFINITION_KEY),
                    hasChangesExcluding(resourceNode, Set.of(DEFINITION_KEY))
            );

            DiffTreeNode definitionNode = child(resourceNode, DEFINITION_KEY);
            appendStep(
                    result,
                    PlanStepType.RESOURCE_DEFINITION,
                    resourcePath + "." + DEFINITION_KEY,
                    definitionNode == null ? null : copyMap(definitionNode.getOldValue()),
                    definitionNode == null ? null : copyMap(definitionNode.getNewValue()),
                    hasChanges(definitionNode)
            );
        }

        return result;
    }

    private void appendStep(PlanResult result,
                            PlanStepType type,
                            String path,
                            Object before,
                            Object after,
                            boolean changed) {
       PlanAction action = resolveAction(before, after, changed);
        result.getSteps().add(new PlanStep(type, path, action, before, after));
        updateSummary(result.getSummary(), action);
    }

    private PlanAction resolveAction(Object before, Object after, boolean changed) {
        if (before == null && after != null) {
            return PlanAction.CREATE;
        }
        if (before != null && after == null) {
            return PlanAction.DELETE;
        }
        if (changed) {
            return PlanAction.UPDATE;
        }
        return PlanAction.NOOP;
    }

    private void updateSummary(PlanSummary summary, PlanAction action) {
        switch (action) {
            case CREATE -> summary.plusCreate();
            case UPDATE -> summary.plusUpdate();
            case DELETE -> summary.plusDelete();
            default -> summary.plusNoop();
        }
    }

    private boolean hasChanges(DiffTreeNode node) {
        if (node == null) {
            return false;
        }
        if (!DiffChangeType.NONE.equals(node.getChangeType())) {
            return true;
        }
        for (DiffTreeNode child : node.getFields().values()) {
            if (hasChanges(child)) {
                return true;
            }
        }
        for (DiffTreeNode child : node.getEntries().values()) {
            if (hasChanges(child)) {
                return true;
            }
        }
        for (DiffTreeNode child : node.getElements()) {
            if (hasChanges(child)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasChangesExcluding(DiffTreeNode node, Set<String> excludedChildren) {
        if (node == null) {
            return false;
        }
        if (!DiffChangeType.NONE.equals(node.getChangeType())) {
            return true;
        }
        for (Map.Entry<String, DiffTreeNode> field : node.getFields().entrySet()) {
            if (excludedChildren.contains(field.getKey())) {
                continue;
            }
            if (hasChanges(field.getValue())) {
                return true;
            }
        }
        for (Map.Entry<String, DiffTreeNode> entry : node.getEntries().entrySet()) {
            if (excludedChildren.contains(entry.getKey())) {
                continue;
            }
            if (hasChanges(entry.getValue())) {
                return true;
            }
        }
        for (DiffTreeNode child : node.getElements()) {
            if (hasChanges(child)) {
                return true;
            }
        }
        return false;
    }

    private DiffTreeNode child(DiffTreeNode node, String childName) {
        if (node == null) {
            return null;
        }
        DiffTreeNode field = node.getFields().get(childName);
        return field != null ? field : node.getEntries().get(childName);
    }

    private Map<String, Object> nestedMap(Object value, String key) {
        Map<String, Object> map = asStringMap(value);
        if (map == null) {
            return Map.of();
        }
        Map<String, Object> nested = asStringMap(map.get(key));
        return nested == null ? Map.of() : nested;
    }

    private Map<String, Object> filterMap(Object value, String excludedKey) {
        Map<String, Object> map = asStringMap(value);
        if (map == null) {
            return null;
        }
        Map<String, Object> filtered = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!excludedKey.equals(entry.getKey())) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }

    private Map<String, Object> copyMap(Object value) {
        Map<String, Object> map = asStringMap(value);
        if (map == null) {
            return null;
        }
        return new LinkedHashMap<>(map);
    }

    private Map<String, Object> asStringMap(Object value) {
        if (!(value instanceof Map<?, ?> map)) {
            return null;
        }
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            copy.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return copy;
    }
}
