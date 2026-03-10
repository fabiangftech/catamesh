package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.ConflictException;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResource;
import dev.catamesh.core.model.PlanResourceType;
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
            PlanAction definitionAction = getDefinitionAction(context, resource);
            if (!PlanAction.NOOP.equals(definitionAction)) {
                continue;
            }

            ResourceDefinition currentDefinition = optionalResourceDefinitionVersionQuery.execute(
                    GetResourceDefinitionDTO.create(resource.getId(), resource.getDefinition().getVersion())
            ).orElseThrow(() -> new InvariantException(
                    String.format(
                            "Resource definition version=%s does not exist for resource=%s",
                            resource.getDefinition().getVersion(),
                            resource.getName()
                    )
            ));

            if (!ResourceDefinition.isSameVersionContent(currentDefinition, resource.getDefinition())) {
                throw new ConflictException(
                        String.format(
                                "Resource definition version %s for resource=%s is immutable. Bump the version to apply definition changes.",
                                resource.getDefinition().getVersion(),
                                resource.getName()
                        )
                );
            }
        }
    }

    private PlanAction getDefinitionAction(ApplyDataProductContext context, Resource resource) {
        return context.getPlan().getResources().stream()
                .filter(planResource -> planResource.getType().equals(PlanResourceType.RESOURCE_DEFINITION))
                .filter(planResource -> planResource.getName().equals(resource.getName()))
                .filter(planResource -> planResource.getVersion().equals(resource.getDefinition().getVersion()))
                .map(PlanResource::getAction)
                .findFirst()
                .orElseThrow(() -> new InvariantException(
                        String.format(
                                "Resource definition action was not calculated for resource=%s version=%s",
                                resource.getName(),
                                resource.getDefinition().getVersion()
                        )
                ));
    }
}
