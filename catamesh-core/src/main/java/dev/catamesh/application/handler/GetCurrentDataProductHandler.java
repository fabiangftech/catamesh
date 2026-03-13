package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.DataProductContext;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;

import java.util.List;
import java.util.Optional;

public class GetCurrentDataProductHandler<Context> extends Handler<Context> {

    private final Query<String, Optional<DataProduct>> optionalDataProductQuery;
    private final Query<String, List<Resource>> allResourcesQuery;
    private final Query<Key, ResourceDefinition> getResourceDefinitionQuery;

    public GetCurrentDataProductHandler(
            Query<String, Optional<DataProduct>> optionalDataProductQuery,
            Query<String, List<Resource>> allResourcesQuery,
            Query<Key, ResourceDefinition> getResourceDefinitionQuery) {
        this.optionalDataProductQuery = optionalDataProductQuery;
        this.allResourcesQuery = allResourcesQuery;
        this.getResourceDefinitionQuery = getResourceDefinitionQuery;
    }

    @Override
    protected void doHandle(Context context) {
        DataProductContext dataProductContext = (DataProductContext) context;
        String dataProductName = dataProductContext.getDesiredDataProduct().getMetadata().getName();
        Optional<DataProduct> optional = optionalDataProductQuery.execute(dataProductName);
        if (optional.isEmpty()) {
            return;
        }
        DataProduct currentDataProduct = optional.get();
        List<Resource> resources = allResourcesQuery.execute(dataProductName);
        resources.forEach(resource -> resource.setDefinition(getResourceDefinitionQuery.execute(resource.getKey())));
        currentDataProduct.setResources(resources);
        dataProductContext.setCurrentDataProduct(currentDataProduct);
    }
}
