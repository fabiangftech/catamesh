package dev.catamesh.core.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public final class PlanEngine {

    private static final String METADATA = "metadata";
    private static final String SPEC = "spec";
    private static final String RESOURCES = "resources";
    private static final String DEFINITION = "definition";

    public PlanResult plan(DataProduct desired, DataProduct current, DiffResult diff) {
        Objects.requireNonNull(diff, "diff is required");

        PlanResult result = new PlanResult();
        DiffTreeNode root = diff.getRoot();

        planMetadata(result, root);
        planSpec(result, root);

        return result;
    }

    private void planMetadata(PlanResult result, DiffTreeNode root) {
        DiffTreeNode metadataNode = child(root, METADATA);

        addStep(
                result,
                PlanStepType.METADATA,
                METADATA,
                copyMap(valueOfOld(metadataNode)),
                copyMap(valueOfNew(metadataNode)),
                hasChanges(metadataNode)
        );
    }

    private void planSpec(PlanResult result, DiffTreeNode root) {
        DiffTreeNode specNode = child(root, SPEC);

        addStep(
                result,
                PlanStepType.SPEC,
                SPEC,
                filterMap(valueOfOld(specNode), RESOURCES),
                filterMap(valueOfNew(specNode), RESOURCES),
                hasChangesExcluding(specNode, Set.of(RESOURCES))
        );

        planResources(result, specNode);
    }

    private void planResources(PlanResult result, DiffTreeNode specNode) {
        DiffTreeNode resourcesNode = child(specNode, RESOURCES);

        Map<String, Object> currentResources = nestedMap(valueOfOld(specNode), RESOURCES);
        Map<String, Object> desiredResources = nestedMap(valueOfNew(specNode), RESOURCES);

        TreeSet<String> resourceNames = new TreeSet<>();
        resourceNames.addAll(currentResources.keySet());
        resourceNames.addAll(desiredResources.keySet());

        for (String resourceName : resourceNames) {
            planResource(result, resourcesNode, resourceName);
        }
    }

    private void planResource(PlanResult result, DiffTreeNode resourcesNode, String resourceName) {
        DiffTreeNode resourceNode = child(resourcesNode, resourceName);
        String resourcePath = SPEC + "." + RESOURCES + "." + resourceName;

        addStep(
                result,
                PlanStepType.RESOURCE,
                resourcePath,
                filterMap(valueOfOld(resourceNode), DEFINITION),
                filterMap(valueOfNew(resourceNode), DEFINITION),
                hasChangesExcluding(resourceNode, Set.of(DEFINITION))
        );

        DiffTreeNode definitionNode = child(resourceNode, DEFINITION);

        addStep(
                result,
                PlanStepType.RESOURCE_DEFINITION,
                resourcePath + "." + DEFINITION,
                copyMap(valueOfOld(definitionNode)),
                copyMap(valueOfNew(definitionNode)),
                hasChanges(definitionNode)
        );
    }

    private void addStep(PlanResult result,
                         PlanStepType type,
                         String path,
                         Object before,
                         Object after,
                         boolean changed) {

        PlanAction action = resolveAction(before, after, changed);
        result.getSteps().add(new PlanStep(type, path, action, before, after));
        updateSummary(result.getSummary(), action);
    }

    private Object valueOfOld(DiffTreeNode node) {
        return node == null ? null : node.getOldValue();
    }

    private Object valueOfNew(DiffTreeNode node) {
        return node == null ? null : node.getNewValue();
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
            case NOOP -> summary.plusNoop();
        }
    }

    private boolean hasChanges(DiffTreeNode node) {
        if (node == null) {
            return false;
        }

        if (node.getChangeType() != DiffChangeType.NONE) {
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

        if (node.getChangeType() != DiffChangeType.NONE) {
            return true;
        }

        for (Map.Entry<String, DiffTreeNode> field : node.getFields().entrySet()) {
            if (!excludedChildren.contains(field.getKey()) && hasChanges(field.getValue())) {
                return true;
            }
        }

        for (Map.Entry<String, DiffTreeNode> entry : node.getEntries().entrySet()) {
            if (!excludedChildren.contains(entry.getKey()) && hasChanges(entry.getValue())) {
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

    private DiffTreeNode child(DiffTreeNode node, String name) {
        if (node == null) {
            return null;
        }

        DiffTreeNode field = node.getFields().get(name);
        return field != null ? field : node.getEntries().get(name);
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
        return map == null ? null : new LinkedHashMap<>(map);
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