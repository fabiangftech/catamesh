package dev.catamesh.application.facade;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.NotFoundException;
import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.*;
import dev.catamesh.core.model.v2.DiffResult;

import java.util.List;
import java.util.Optional;

public class DefaultDataProductFacade implements DataProductFacade {

    private static final String NOT_FOUND_DATA_PRODUCT = "Data product '%s' was not found";
    private final Factory<Void, Handler<ApplyDataProductContext>> applyDataProductPipelineFactory;
    private final Factory<Void, Handler<ApplyDataProductContext>> planDataProductPipelineFactory;
    private final Factory<Void, Handler<ApplyDataProductContext>> diffDataProductPipelineFactory;
    private final Factory<Void, Handler<DestroyDataProductContext>> planDestroyDataProductPipelineFactory;
    private final Factory<Void, Handler<DestroyDataProductContext>> applyDestroyDataProductPipelineFactory;
    private final Query<String, Optional<DataProduct>> optionalDataProductQuery;
    private final Query<String, List<Resource>> allResourcesQuery;
    private final Query<Key, ResourceDefinition> getResourceDefinitionQuery;

    public DefaultDataProductFacade(Factory<Void, Handler<ApplyDataProductContext>> applyDataProductPipelineFactory,
                                    Factory<Void, Handler<ApplyDataProductContext>> planDataProductPipelineFactory,
                                    Factory<Void, Handler<ApplyDataProductContext>> diffDataProductPipelineFactory,
                                    Factory<Void, Handler<DestroyDataProductContext>> planDestroyDataProductPipelineFactory,
                                    Factory<Void, Handler<DestroyDataProductContext>> applyDestroyDataProductPipelineFactory,
                                    Query<String, Optional<DataProduct>> optionalDataProductQuery,
                                    Query<String, List<Resource>> allResourcesQuery,
                                    Query<Key, ResourceDefinition> getResourceDefinitionQuery) {
        this.applyDataProductPipelineFactory = applyDataProductPipelineFactory;
        this.planDataProductPipelineFactory = planDataProductPipelineFactory;
        this.diffDataProductPipelineFactory = diffDataProductPipelineFactory;
        this.planDestroyDataProductPipelineFactory = planDestroyDataProductPipelineFactory;
        this.applyDestroyDataProductPipelineFactory = applyDestroyDataProductPipelineFactory;
        this.optionalDataProductQuery = optionalDataProductQuery;
        this.allResourcesQuery = allResourcesQuery;
        this.getResourceDefinitionQuery = getResourceDefinitionQuery;

    }

    @Override
    public Plan plan(String yaml) {
        Handler<ApplyDataProductContext> chain = planDataProductPipelineFactory.create();
        ApplyDataProductContext context = ApplyDataProductContext.create(yaml);
        chain.handle(context);
        return context.getPlan();
    }

    @Override
    public DiffResult diff(String yaml) {
        Handler<ApplyDataProductContext> chain = diffDataProductPipelineFactory.create();
        ApplyDataProductContext context = ApplyDataProductContext.create(yaml);
        chain.handle(context);
        return context.getDiffResult();
    }

    @Override
    public ApplyResult apply(String yaml) {
        Handler<ApplyDataProductContext> chain = applyDataProductPipelineFactory.create();
        ApplyDataProductContext context = ApplyDataProductContext.create(yaml);
        chain.handle(context);
        return new ApplyResult(context.getPlan(), get(context.getDataProductName()));
    }

    @Override
    public DataProduct get(String name) {
        Optional<DataProduct> optionalDataProduct = optionalDataProductQuery.execute(name);
        if (optionalDataProduct.isEmpty()) {
            throw new NotFoundException(String.format(NOT_FOUND_DATA_PRODUCT, name));
        }

        DataProduct dataProduct = optionalDataProduct.get();
        List<Resource> resources = allResourcesQuery.execute(name);
        resources.forEach(resource -> {
            ResourceDefinition resourceDefinition = getResourceDefinitionQuery.execute(resource.getKey());
            resource.setDefinition(resourceDefinition);
        });
        dataProduct.setResources(resources);
        return dataProduct;
    }

    @Override
    public Plan planDestroy(String yaml) {
        Handler<DestroyDataProductContext> chain = planDestroyDataProductPipelineFactory.create();
        DestroyDataProductContext context = DestroyDataProductContext.createForPlan(yaml);
        chain.handle(context);
        return context.getPlan();
    }

    @Override
    public void applyDestroy(String yaml) {
        Handler<DestroyDataProductContext> chain = applyDestroyDataProductPipelineFactory.create();
        chain.handle(DestroyDataProductContext.createForApply(yaml));
    }
}
