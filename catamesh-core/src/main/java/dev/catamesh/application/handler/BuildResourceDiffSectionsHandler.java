package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.*;

import java.util.*;

public class BuildResourceDiffSectionsHandler extends Handler<ApplyDataProductContext> {

    private final DiffComparisonSupport diffComparisonSupport;

    public BuildResourceDiffSectionsHandler(DiffComparisonSupport diffComparisonSupport) {
        this.diffComparisonSupport = diffComparisonSupport;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        DataProduct desired = context.getDataProduct();
        DataProduct current = context.getCurrentDataProduct();

        Map<String, Resource> desiredByName = diffComparisonSupport.byResourceName(desired.getSpec().getResources());
        Map<String, Resource> currentByName = current == null
                ? Collections.emptyMap()
                : diffComparisonSupport.byResourceName(current.getSpec().getResources());

        SortedSet<String> resourceNames = new TreeSet<>();
        resourceNames.addAll(desiredByName.keySet());
        resourceNames.addAll(currentByName.keySet());

        for (String resourceName : resourceNames) {
            Resource desiredResource = desiredByName.get(resourceName);
            Resource currentResource = currentByName.get(resourceName);
            List<DiffChange> changes = diffComparisonSupport.compareResource(desiredResource, currentResource);
            if (!changes.isEmpty()) {
                context.addDiffSection(new DiffSection(
                        DiffScope.RESOURCE,
                        resourceName,
                        diffComparisonSupport.sortByPath(changes)
                ));
            }
        }
    }
}
