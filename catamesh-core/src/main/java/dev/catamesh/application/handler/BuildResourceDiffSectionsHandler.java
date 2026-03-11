package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.DiffChange;
import dev.catamesh.core.model.DiffScope;
import dev.catamesh.core.model.DiffSection;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.strategy.DiffStrategy;
import dev.catamesh.infrastructure.adapter.DiffPayloadAdapter;

import java.util.Collections;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

@Deprecated
public class BuildResourceDiffSectionsHandler extends Handler<ApplyDataProductContext> {

    private final DiffStrategy<Resource> diffStrategy;

    public BuildResourceDiffSectionsHandler(DiffStrategy<Resource> diffStrategy) {
        this.diffStrategy = diffStrategy;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        DataProduct desired = context.getDataProduct();
        DataProduct current = context.getCurrentDataProduct();

        Map<String, Resource> desiredByName = DiffPayloadAdapter.byResourceName(desired.getSpec().getResources());
        Map<String, Resource> currentByName = current == null
                ? Collections.emptyMap()
                : DiffPayloadAdapter.byResourceName(current.getSpec().getResources());

        SortedSet<String> resourceNames = resourceNames(desiredByName, currentByName);

        for (String resourceName : resourceNames) {
            Resource desiredResource = desiredByName.get(resourceName);
            Resource currentResource = currentByName.get(resourceName);
            java.util.List<DiffChange> changes = diffStrategy.compare(desiredResource, currentResource, "");
            if (!changes.isEmpty()) {
                context.addDiffSection(new DiffSection(
                        DiffScope.RESOURCE,
                        resourceName,
                        changes
                ));
            }
        }
    }

    private SortedSet<String> resourceNames(Map<String, Resource> desiredByName, Map<String, Resource> currentByName) {
        SortedSet<String> resourceNames = new TreeSet<>();
        resourceNames.addAll(desiredByName.keySet());
        resourceNames.addAll(currentByName.keySet());
        return resourceNames;
    }
}
