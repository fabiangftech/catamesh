package dev.catamesh.application.strategy;

import dev.catamesh.infrastructure.adapter.DiffTreeAdapter;
import dev.catamesh.infrastructure.adapter.MapAdapter;
import dev.catamesh.core.model.DiffTreeNode;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.core.model.PlanStepType;
import dev.catamesh.core.strategy.PlanStrategy;

import java.util.Set;

public class PlanSpecStrategy implements PlanStrategy {

    private final PlanStrategy planResourcesStrategy;

    public PlanSpecStrategy(PlanStrategy planResourcesStrategy) {
        this.planResourcesStrategy = planResourcesStrategy;
    }

    @Override
    public void plan(PlanResult result, DiffTreeNode root) {
        DiffTreeNode specNode = DiffTreeAdapter.child(root, SPEC);

        addStep(
                result,
                PlanStepType.SPEC,
                SPEC,
                MapAdapter.filterMap(valueOfOld(specNode), RESOURCES),
                MapAdapter.filterMap(valueOfNew(specNode), RESOURCES),
                DiffTreeNode.hasChangesExcluding(specNode, Set.of(RESOURCES))
        );

        planResourcesStrategy.plan(result, specNode);
    }
}
