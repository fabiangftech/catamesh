package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.*;
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
            PlanAction resourceAction = getResourceAction(context, resource.getName());
            String definitionVersion = resource.getDefinition().getVersion();

            if (resourceAction.equals(PlanAction.CREATE)) {
                context.plusCreateSummary();
                context.getPlan().addResource(
                        PlanResource.resourceDefinition(resource.getName(), definitionVersion, PlanAction.CREATE)
                );
                continue;
            }

            String resourceId = resolveResourceId(context, resource);
            boolean definitionAlreadyExists = optionalResourceDefinitionQuery.execute(
                    GetResourceDefinitionDTO.create(resourceId, definitionVersion)
            ).isPresent();

            if (definitionAlreadyExists) {
                context.plusNoopSummary();
                context.getPlan().addResource(
                        PlanResource.resourceDefinition(resource.getName(), definitionVersion, PlanAction.NOOP)
                );
                continue;
            }

            context.plusCreateSummary();
            context.getPlan().addResource(
                    PlanResource.resourceDefinition(resource.getName(), definitionVersion, PlanAction.CREATE)
            );
        }
    }

    private PlanAction getResourceAction(ApplyDataProductContext context, String resourceName) {
        return context.getPlan().getResources().stream()
                .filter(resource -> resource.getType().equals(PlanResourceType.RESOURCE))
                .filter(resource -> resource.getName().equals(resourceName))
                .map(PlanResource::getAction)
                .findFirst()
                .orElseThrow(() -> new InvariantException(
                        String.format("Resource action was not calculated for resource=%s", resourceName)
                ));
    }

    private String resolveResourceId(ApplyDataProductContext context, Resource resource) {
        String resourceId = Optional.ofNullable(resource.getId())
                .orElseGet(() -> optionalResourceByNameAndDataProductIdQuery.execute(
                                GetResourceDTO.create(
                                        resource.getName(),
                                        context.getDataProduct().getMetadata().getId()
                                )
                        )
                        .map(Resource::getId)
                        .orElseThrow(() -> new InvariantException(
                                String.format("Resource does not exist for name=%s", resource.getName())
                        )));

        resource.setId(Key.create(resourceId));
        return resourceId;
    }
}
