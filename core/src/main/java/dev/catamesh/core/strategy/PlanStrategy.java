package dev.catamesh.core.strategy;

import dev.catamesh.infrastructure.adapter.MapAdapter;
import dev.catamesh.core.model.*;

import java.util.LinkedHashMap;
import java.util.Map;

public interface PlanStrategy {
    String METADATA = "metadata";
    String SPEC = "spec";
    String RESOURCES = "resources";
    String DEFINITION = "definition";


    void plan(PlanResult result, DiffTreeNode root);

    default void addStep(PlanResult result,
                         PlanStepType type,
                         String path,
                         Object before,
                         Object after,
                         boolean changed) {

        PlanAction action = resolveAction(before, after, changed);
        result.getSteps().add(new PlanStep(type, path, action, before, after));
        updateSummary(result.getSummary(), action);
    }

    default Object valueOfOld(DiffTreeNode node) {
        return node == null ? null : node.getOldValue();
    }

    default Object valueOfNew(DiffTreeNode node) {
        return node == null ? null : node.getNewValue();
    }

    default PlanAction resolveAction(Object before, Object after, boolean changed) {
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

    default void updateSummary(PlanSummary summary, PlanAction action) {
        switch (action) {
            case CREATE -> summary.plusCreate();
            case UPDATE -> summary.plusUpdate();
            case DELETE -> summary.plusDelete();
            case NOOP -> summary.plusNoop();
            default -> throw new IllegalArgumentException();
        }
    }

    default Map<String, Object> copyMap(Object value) {
        Map<String, Object> map = MapAdapter.asStringMap(value);
        return map == null ? null : new LinkedHashMap<>(map);
    }
}
