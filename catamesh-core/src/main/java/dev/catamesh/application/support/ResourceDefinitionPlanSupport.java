package dev.catamesh.application.support;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResource;
import dev.catamesh.core.model.PlanResourceType;
import dev.catamesh.core.model.Resource;
import dev.catamesh.infrastructure.dto.GetResourceDTO;

import java.util.Optional;

public final class ResourceDefinitionPlanSupport {

    private ResourceDefinitionPlanSupport() {
        // utility class
    }

    public static PlanAction getResourceAction(ApplyDataProductContext context, String resourceName) {
        return context.getPlan().getResources().stream()
                .filter(resource -> resource.getType().equals(PlanResourceType.RESOURCE))
                .filter(resource -> resource.getName().equals(resourceName))
                .map(PlanResource::getAction)
                .findFirst()
                .orElseThrow(() -> new InvariantException(
                        String.format("Resource action was not calculated for resource=%s", resourceName)
                ));
    }

    public static PlanAction getDefinitionAction(ApplyDataProductContext context, Resource resource) {
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

    public static String resolveResourceId(ApplyDataProductContext context,
                                           Resource resource,
                                           Query<GetResourceDTO, Optional<Resource>> optionalResourceByNameAndDataProductIdQuery) {
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

    public static void addDefinitionPlan(ApplyDataProductContext context, Resource resource, PlanAction action) {
        updateSummary(context, action);
        context.getPlan().addResource(
                PlanResource.resourceDefinition(resource.getName(), resource.getDefinition().getVersion(), action)
        );
    }

    private static void updateSummary(ApplyDataProductContext context, PlanAction action) {
        if (PlanAction.CREATE.equals(action)) {
            context.plusCreateSummary();
            return;
        }
        if (PlanAction.NOOP.equals(action)) {
            context.plusNoopSummary();
            return;
        }
        throw new InvariantException(
                String.format("Unsupported definition plan action=%s", action)
        );
    }
}
