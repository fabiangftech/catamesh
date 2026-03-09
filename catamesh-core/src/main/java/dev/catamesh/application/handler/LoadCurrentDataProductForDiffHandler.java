package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;

import java.util.List;
import java.util.Optional;

public class LoadCurrentDataProductForDiffHandler extends Handler<ApplyDataProductContext> {

    private final Query<String, Optional<DataProduct>> optionalDataProductQuery;
    private final Query<String, List<Resource>> allResourcesQuery;
    private final Query<Key, ResourceDefinition> getResourceDefinitionQuery;

    public LoadCurrentDataProductForDiffHandler(
            Query<String, Optional<DataProduct>> optionalDataProductQuery,
            Query<String, List<Resource>> allResourcesQuery,
            Query<Key, ResourceDefinition> getResourceDefinitionQuery) {
        this.optionalDataProductQuery = optionalDataProductQuery;
        this.allResourcesQuery = allResourcesQuery;
        this.getResourceDefinitionQuery = getResourceDefinitionQuery;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        Optional<DataProduct> optionalCurrent = optionalDataProductQuery.execute(context.getDataProductName());
        if (optionalCurrent.isEmpty()) {
            context.setCurrentDataProduct(null);
            return;
        }

        DataProduct current = optionalCurrent.get();
        List<Resource> resources = allResourcesQuery.execute(context.getDataProductName());
        resources.forEach(resource -> resource.setDefinition(getResourceDefinitionQuery.execute(resource.getKey())));
        current.setResources(resources);
        context.setCurrentDataProduct(current);
    }
}
