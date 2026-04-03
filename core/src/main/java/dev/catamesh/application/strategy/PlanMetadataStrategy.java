package dev.catamesh.application.strategy;

import dev.catamesh.infrastructure.adapter.DiffTreeAdapter;
import dev.catamesh.core.model.DiffTreeNode;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.core.model.PlanStepType;
import dev.catamesh.core.strategy.PlanStrategy;

public class PlanMetadataStrategy implements PlanStrategy {
    @Override
    public void plan(PlanResult result, DiffTreeNode root) {
        DiffTreeNode metadataNode = DiffTreeAdapter.child(root, METADATA);
        addStep(
                result,
                PlanStepType.METADATA,
                METADATA,
                copyMap(valueOfOld(metadataNode)),
                copyMap(valueOfNew(metadataNode)),
                DiffTreeNode.hasChanges(metadataNode)
        );
    }
}
