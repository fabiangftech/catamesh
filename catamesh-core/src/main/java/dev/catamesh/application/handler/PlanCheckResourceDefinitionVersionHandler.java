package dev.catamesh.application.handler;

import dev.catamesh.application.support.ResourceDefinitionPlanSupport;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.Resource;
import dev.catamesh.infrastructure.dto.GetResourceDTO;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;

import java.util.Optional;

public class PlanCheckResourceDefinitionVersionHandler extends Handler<ApplyDataProductContext> {

    private final Query<GetResourceDefinitionDTO, Optional<String>> optionalResourceDefinitionQuery;
    private final Query<GetResourceDTO, Optional<Resource>> optionalResourceByNameAndDataProductIdQuery;

    public PlanCheckResourceDefinitionVersionHandler(
            Query<GetResourceDefinitionDTO, Optional<String>> optionalResourceDefinitionQuery,
            Query<GetResourceDTO, Optional<Resource>> optionalResourceByNameAndDataProductIdQuery) {
        this.optionalResourceDefinitionQuery = optionalResourceDefinitionQuery;
        this.optionalResourceByNameAndDataProductIdQuery = optionalResourceByNameAndDataProductIdQuery;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        for (Resource resource : context.getResources()) {
            applyDefinitionPlan(context, resource);
        }
    }

    private void applyDefinitionPlan(ApplyDataProductContext context, Resource resource) {
        PlanAction action = resolveDefinitionAction(context, resource);
        ResourceDefinitionPlanSupport.addDefinitionPlan(context, resource, action);
    }

    private PlanAction resolveDefinitionAction(ApplyDataProductContext context, Resource resource) {
        if (PlanAction.CREATE.equals(ResourceDefinitionPlanSupport.getResourceAction(context, resource.getName()))) {
            return PlanAction.CREATE;
        }

        String resourceId = ResourceDefinitionPlanSupport.resolveResourceId(
                context,
                resource,
                optionalResourceByNameAndDataProductIdQuery
        );
        return definitionExists(resourceId, resource.getDefinition().getVersion())
                ? PlanAction.NOOP
                : PlanAction.CREATE;
    }

    private boolean definitionExists(String resourceId, String definitionVersion) {
        return optionalResourceDefinitionQuery.execute(
                GetResourceDefinitionDTO.create(resourceId, definitionVersion)
        ).isPresent();
    }
}
