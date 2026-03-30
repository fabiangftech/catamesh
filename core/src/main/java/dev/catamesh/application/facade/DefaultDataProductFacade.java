package dev.catamesh.application.facade;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.NotFoundException;
import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.*;
import dev.catamesh.core.model.*;
import dev.catamesh.infrastructure.dto.GetResourceDTO;

import java.util.List;
import java.util.Optional;

public class DefaultDataProductFacade implements DataProductFacade {
    private final Factory<Void, Handler<ValidateDataProductContext>> validateDataProductChainFactory;
    private final Factory<Void, Handler<DiffDataProductContext>> diffDataProductChainFactory;
    private final Factory<Void, Handler<PlanDataProductContext>> planDataProductChainFactory;
    private final Factory<Void, Handler<ApplyDataProductContext>> applyDataProductChainFactory;
    private final Query<String, Optional<DataProduct>> optionalDataProductQuery;
    private final Query<String, List<Resource>> allResourcesQuery;
    private final Query<Key, ResourceDefinition> getResourceDefinitionQuery;

    public DefaultDataProductFacade(
            Factory<Void, Handler<ValidateDataProductContext>> validateDataProductChainFactory,
            Factory<Void, Handler<DiffDataProductContext>> diffDataProductChainFactory,
            Factory<Void, Handler<PlanDataProductContext>> planDataProductChainFactory,
            Factory<Void, Handler<ApplyDataProductContext>> applyDataProductChainFactory,
            Query<String, Optional<DataProduct>> optionalDataProductQuery,
            Query<String, List<Resource>> allResourcesQuery,
            Query<Key, ResourceDefinition> getResourceDefinitionQuery) {
        this.validateDataProductChainFactory = validateDataProductChainFactory;
        this.diffDataProductChainFactory = diffDataProductChainFactory;
        this.planDataProductChainFactory = planDataProductChainFactory;
        this.applyDataProductChainFactory = applyDataProductChainFactory;
        this.optionalDataProductQuery = optionalDataProductQuery;
        this.allResourcesQuery = allResourcesQuery;
        this.getResourceDefinitionQuery = getResourceDefinitionQuery;
    }

    @Override
    public ValidateResult validate(String yaml) {
        Handler<ValidateDataProductContext> chain = validateDataProductChainFactory.create();
        ValidateDataProductContext context = ValidateDataProductContext.create(yaml);
        chain.handle(context);
        return context.getValidateResult();
    }

    @Override
    public DiffResult diff(String yaml) {
        Handler<DiffDataProductContext> chain = diffDataProductChainFactory.create();
        DiffDataProductContext context = DiffDataProductContext.create(yaml);
        chain.handle(context);
        return context.getDiffResult();
    }

    @Override
    public PlanResult plan(String yaml) {
        Handler<PlanDataProductContext> chain = planDataProductChainFactory.create();
        PlanDataProductContext context = PlanDataProductContext.create(yaml);
        chain.handle(context);
        PlanResult planResult = context.getPlanResult();
        planResult.setPolicyRules(context.getPlanResult().getPolicyRules());
        return context.getPlanResult();
    }

    @Override
    public ApplyResult apply(String yaml) {
        Handler<ApplyDataProductContext> chain = applyDataProductChainFactory.create();
        ApplyDataProductContext context = ApplyDataProductContext.create(yaml);
        chain.handle(context);
        ApplyResult applyResult = context.getApplyResult();
        applyResult.setPolicyRules(context.getValidateResult().getPolicyRules());
        return applyResult;
    }

    @Override
    public DataProduct get(String dataProductName) {
        DataProduct dataProduct = this.optionalDataProductQuery.execute(dataProductName)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Data product not found: %s", dataProductName))
                );
        List<Resource> resources = allResourcesQuery.execute(dataProduct.getMetadata().getName());
        for (Resource resource : resources) {
            ResourceDefinition resourceDefinition = getResourceDefinitionQuery.execute(resource.getKey());
            resource.setDefinition(resourceDefinition);
        }
        dataProduct.setResources(resources);
        return dataProduct;
    }

}
