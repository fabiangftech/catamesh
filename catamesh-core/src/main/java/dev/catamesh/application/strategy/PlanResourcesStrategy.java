package dev.catamesh.application.strategy;

import dev.catamesh.application.adapter.DiffTreeAdapter;
import dev.catamesh.application.adapter.MapAdapter;
import dev.catamesh.core.model.DiffTreeNode;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.core.model.PlanStepType;
import dev.catamesh.core.strategy.PlanStrategy;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PlanResourcesStrategy implements PlanStrategy {
    @Override
    public void plan(PlanResult result, DiffTreeNode root) {
        DiffTreeNode resourcesNode = DiffTreeAdapter.child(root, RESOURCES);

        Map<String, Object> currentResources = MapAdapter.nestedMap(valueOfOld(root), RESOURCES);
        Map<String, Object> desiredResources = MapAdapter.nestedMap(valueOfNew(root), RESOURCES);

        TreeSet<String> resourceNames = new TreeSet<>();
        resourceNames.addAll(currentResources.keySet());
        resourceNames.addAll(desiredResources.keySet());

        for (String resourceName : resourceNames) {
            plan(result, resourcesNode, resourceName);
        }
    }

    private void plan(PlanResult result, DiffTreeNode resourcesNode, String resourceName) {
        DiffTreeNode resourceNode = DiffTreeAdapter.child(resourcesNode, resourceName);
        String resourcePath = SPEC + "." + RESOURCES + "." + resourceName;

        addStep(
                result,
                PlanStepType.RESOURCE,
                resourcePath,
                MapAdapter.filterMap(valueOfOld(resourceNode), DEFINITION),
                MapAdapter.filterMap(valueOfNew(resourceNode), DEFINITION),
                DiffTreeNode.hasChangesExcluding(resourceNode, Set.of(DEFINITION))
        );

        DiffTreeNode definitionNode = DiffTreeAdapter.child(resourceNode, DEFINITION);

        addStep(
                result,
                PlanStepType.RESOURCE_DEFINITION,
                resourcePath + "." + DEFINITION,
                copyMap(valueOfOld(definitionNode)),
                copyMap(valueOfNew(definitionNode)),
                DiffTreeNode.hasChanges(definitionNode)
        );
    }
}
