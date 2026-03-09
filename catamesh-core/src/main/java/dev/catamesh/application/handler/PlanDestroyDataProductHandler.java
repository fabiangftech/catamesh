package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.*;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

public class PlanDestroyDataProductHandler extends Handler<DestroyDataProductContext> {

    private final Query<GetResourceDefinitionDTO, Optional<String>> optionalResourceDefinitionQuery;
    private final Query<Key, Integer> countResourceDefinitionsByResourceIdQuery;

    public PlanDestroyDataProductHandler(
            Query<GetResourceDefinitionDTO, Optional<String>> optionalResourceDefinitionQuery,
            Query<Key, Integer> countResourceDefinitionsByResourceIdQuery) {
        this.optionalResourceDefinitionQuery = optionalResourceDefinitionQuery;
        this.countResourceDefinitionsByResourceIdQuery = countResourceDefinitionsByResourceIdQuery;
    }

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        Plan plan = new Plan(context.getName());
        context.setPlan(plan);

        Map<String, LinkedHashSet<String>> requestedDefinitionVersionsByResource = buildRequestedDefinitionVersionsByResource(context);
        requestedDefinitionVersionsByResource.forEach((resourceName, definitionVersions) ->
                appendResourcePlan(context, plan, resourceName, definitionVersions)
        );

        boolean hasDeletes = plan.getResources().stream()
                .filter(planResource -> planResource.getAction().equals(PlanAction.DELETE))
                .findAny()
                .isPresent();
        if (!hasDeletes) {
            plan.setAction(PlanAction.NOOP);
            context.plusNoopSummary();
            return;
        }

        long deleteResources = plan.getResources().stream()
                .filter(planResource -> planResource.getType().equals(PlanResourceType.RESOURCE))
                .filter(planResource -> planResource.getAction().equals(PlanAction.DELETE))
                .count();

        int remainingResources = context.getResources().size() - (int) deleteResources;
        if (remainingResources <= 0) {
            plan.setAction(PlanAction.DELETE);
            context.plusDeleteSummary();
            return;
        }
        plan.setAction(PlanAction.NOOP);
        context.plusNoopSummary();
    }

    private Map<String, LinkedHashSet<String>> buildRequestedDefinitionVersionsByResource(DestroyDataProductContext context) {
        Map<String, LinkedHashSet<String>> requestedDefinitionVersionsByResource = new LinkedHashMap<>();
        context.getRequestedResources().forEach(resource -> requestedDefinitionVersionsByResource
                .computeIfAbsent(resource.getName(), key -> new LinkedHashSet<>())
                .add(resource.getDefinition().getVersion()));
        return requestedDefinitionVersionsByResource;
    }

    private void appendResourcePlan(DestroyDataProductContext context,
                                    Plan plan,
                                    String resourceName,
                                    LinkedHashSet<String> definitionVersions) {
        Optional<Resource> currentResource = context.getResources().stream()
                .filter(resource -> resource.getName().equals(resourceName))
                .findFirst();
        if (currentResource.isEmpty()) {
            definitionVersions.forEach(version -> {
                plan.addResource(PlanResource.resourceDefinition(resourceName, version, PlanAction.NOOP));
                context.plusNoopSummary();
            });
            plan.addResource(PlanResource.resource(resourceName, PlanAction.NOOP));
            context.plusNoopSummary();
            return;
        }

        Resource existingResource = currentResource.get();
        int definitionCount = countResourceDefinitionsByResourceIdQuery.execute(existingResource.getKey());
        int definitionDeletes = 0;
        for (String definitionVersion : definitionVersions) {
            boolean exists = optionalResourceDefinitionQuery.execute(
                    GetResourceDefinitionDTO.create(existingResource.getId(), definitionVersion)
            ).isPresent();
            if (exists) {
                plan.addResource(PlanResource.resourceDefinition(resourceName, definitionVersion, PlanAction.DELETE));
                context.plusDeleteSummary();
                definitionDeletes++;
                continue;
            }
            plan.addResource(PlanResource.resourceDefinition(resourceName, definitionVersion, PlanAction.NOOP));
            context.plusNoopSummary();
        }

        if (definitionDeletes > 0 && (definitionCount - definitionDeletes) <= 0) {
            plan.addResource(PlanResource.resource(resourceName, PlanAction.DELETE));
            context.plusDeleteSummary();
            return;
        }
        plan.addResource(PlanResource.resource(resourceName, PlanAction.NOOP));
        context.plusNoopSummary();
    }
}
