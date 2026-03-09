package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.Resource;

import java.util.List;
import java.util.Objects;

public class GetResourcesForDestroyHandler extends Handler<DestroyDataProductContext> {

    private final Query<String, List<Resource>> allResourcesQuery;

    public GetResourcesForDestroyHandler(Query<String, List<Resource>> allResourcesQuery) {
        this.allResourcesQuery = allResourcesQuery;
    }

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        if (Objects.isNull(context.getDataProduct())) {
            return;
        }
        context.getDataProduct().setResources(allResourcesQuery.execute(context.getName()));
    }
}
