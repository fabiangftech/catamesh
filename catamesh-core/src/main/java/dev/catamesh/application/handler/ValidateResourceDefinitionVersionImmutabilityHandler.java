package dev.catamesh.application.handler;

import dev.catamesh.application.support.ResourceDefinitionPlanSupport;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.ImmutableResourceDefinitionVersionException;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;

import java.util.Optional;

public class ValidateResourceDefinitionVersionImmutabilityHandler extends Handler<ApplyDataProductContext> {

    private final Query<GetResourceDefinitionDTO, Optional<ResourceDefinition>> optionalResourceDefinitionVersionQuery;

    public ValidateResourceDefinitionVersionImmutabilityHandler(
            Query<GetResourceDefinitionDTO, Optional<ResourceDefinition>> optionalResourceDefinitionVersionQuery) {
        this.optionalResourceDefinitionVersionQuery = optionalResourceDefinitionVersionQuery;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        for (Resource resource : context.getResources()) {
            validateResourceDefinition(context, resource);
        }
    }

    private void validateResourceDefinition(ApplyDataProductContext context, Resource resource) {
        if (!shouldValidate(context, resource)) {
            return;
        }

        ResourceDefinition currentDefinition = loadCurrentDefinition(resource);
        ensureImmutableContent(resource, currentDefinition);
    }

    private boolean shouldValidate(ApplyDataProductContext context, Resource resource) {
        return PlanAction.NOOP.equals(ResourceDefinitionPlanSupport.getDefinitionAction(context, resource));
    }

    private ResourceDefinition loadCurrentDefinition(Resource resource) {
        return optionalResourceDefinitionVersionQuery.execute(
                GetResourceDefinitionDTO.create(resource.getId(), resource.getDefinition().getVersion())
        ).orElseThrow(() -> missingDefinition(resource));
    }

    private InvariantException missingDefinition(Resource resource) {
        return new InvariantException(
                String.format(
                        "Resource definition version=%s does not exist for resource=%s",
                        resource.getDefinition().getVersion(),
                        resource.getName()
                )
        );
    }

    private void ensureImmutableContent(Resource resource, ResourceDefinition currentDefinition) {
        if (ResourceDefinition.isSameVersionContent(currentDefinition, resource.getDefinition())) {
            return;
        }

        throw new ImmutableResourceDefinitionVersionException(
                resource.getName(),
                resource.getDefinition().getVersion(),
                ResourceDefinition.immutableDifferences(currentDefinition, resource.getDefinition())
        );
    }
}
