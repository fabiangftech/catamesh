package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.*;
import dev.catamesh.infrastructure.adapter.DestroyRequestAdapter;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;

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
        Plan plan = initializePlan(context);
        buildResourcePlans(context, plan);
        resolvePlanAction(context, plan);
    }

    private Plan initializePlan(DestroyDataProductContext context) {
        Plan plan = new Plan(context.getName());
        context.setPlan(plan);
        return plan;
    }

    private void buildResourcePlans(DestroyDataProductContext context, Plan plan) {
        requestedDefinitionVersionsByResource(context).forEach((resourceName, definitionVersions) ->
                appendResourcePlan(context, plan, resourceName, definitionVersions)
        );
    }

    private Map<String, LinkedHashSet<String>> requestedDefinitionVersionsByResource(DestroyDataProductContext context) {
        return DestroyRequestAdapter.requestedDefinitionVersionsByResource(context.getRequestedResources());
    }

    private void resolvePlanAction(DestroyDataProductContext context, Plan plan) {
        if (!hasDeleteActions(plan)) {
            markNoop(plan, context);
            return;
        }
        if (removesAllResources(context, plan)) {
            markDelete(plan, context);
            return;
        }
        markNoop(plan, context);
    }

    private boolean hasDeleteActions(Plan plan) {
        return plan.getResources().stream()
                .anyMatch(planResource -> planResource.getAction().equals(PlanAction.DELETE));
    }

    private boolean removesAllResources(DestroyDataProductContext context, Plan plan) {
        return remainingResources(context, plan) <= 0;
    }

    private int remainingResources(DestroyDataProductContext context, Plan plan) {
        return context.getResources().size() - (int) deletedResources(plan);
    }

    private long deletedResources(Plan plan) {
        return plan.getResources().stream()
                .filter(planResource -> planResource.getType().equals(PlanResourceType.RESOURCE))
                .filter(planResource -> planResource.getAction().equals(PlanAction.DELETE))
                .count();
    }

    private void appendResourcePlan(DestroyDataProductContext context,
                                    Plan plan,
                                    String resourceName,
                                    LinkedHashSet<String> definitionVersions) {
        Optional<Resource> currentResource = findCurrentResource(context, resourceName);
        if (currentResource.isEmpty()) {
            appendMissingResourcePlan(context, plan, resourceName, definitionVersions);
            return;
        }

        appendExistingResourcePlan(context, plan, currentResource.get(), resourceName, definitionVersions);
    }

    private Optional<Resource> findCurrentResource(DestroyDataProductContext context, String resourceName) {
        return context.getResources().stream()
                .filter(resource -> resource.getName().equals(resourceName))
                .findFirst();
    }

    private void appendMissingResourcePlan(DestroyDataProductContext context,
                                           Plan plan,
                                           String resourceName,
                                           LinkedHashSet<String> definitionVersions) {
        for (String definitionVersion : definitionVersions) {
            appendDefinitionPlan(plan, context, resourceName, definitionVersion, PlanAction.NOOP);
        }
        appendResourceEntry(plan, context, resourceName, PlanAction.NOOP);
    }

    private void appendExistingResourcePlan(DestroyDataProductContext context,
                                            Plan plan,
                                            Resource existingResource,
                                            String resourceName,
                                            LinkedHashSet<String> definitionVersions) {
        int definitionCount = countResourceDefinitionsByResourceIdQuery.execute(existingResource.getKey());
        int definitionDeletes = appendDefinitionPlans(plan, context, existingResource, resourceName, definitionVersions);
        appendResourceEntry(plan, context, resourceName, resourceAction(definitionCount, definitionDeletes));
    }

    private int appendDefinitionPlans(Plan plan,
                                      DestroyDataProductContext context,
                                      Resource existingResource,
                                      String resourceName,
                                      LinkedHashSet<String> definitionVersions) {
        int definitionDeletes = 0;
        for (String definitionVersion : definitionVersions) {
            PlanAction action = definitionAction(existingResource, definitionVersion);
            appendDefinitionPlan(plan, context, resourceName, definitionVersion, action);
            if (PlanAction.DELETE.equals(action)) {
                definitionDeletes++;
            }
        }
        return definitionDeletes;
    }

    private PlanAction definitionAction(Resource existingResource, String definitionVersion) {
        boolean exists = optionalResourceDefinitionQuery.execute(
                GetResourceDefinitionDTO.create(existingResource.getId(), definitionVersion)
        ).isPresent();
        return exists ? PlanAction.DELETE : PlanAction.NOOP;
    }

    private PlanAction resourceAction(int definitionCount, int definitionDeletes) {
        if (definitionDeletes > 0 && (definitionCount - definitionDeletes) <= 0) {
            return PlanAction.DELETE;
        }
        return PlanAction.NOOP;
    }

    private void appendDefinitionPlan(Plan plan,
                                      DestroyDataProductContext context,
                                      String resourceName,
                                      String definitionVersion,
                                      PlanAction action) {
        plan.addResource(PlanResource.resourceDefinition(resourceName, definitionVersion, action));
        updateSummary(context, action);
    }

    private void appendResourceEntry(Plan plan,
                                     DestroyDataProductContext context,
                                     String resourceName,
                                     PlanAction action) {
        plan.addResource(PlanResource.resource(resourceName, action));
        updateSummary(context, action);
    }

    private void updateSummary(DestroyDataProductContext context, PlanAction action) {
        if (PlanAction.DELETE.equals(action)) {
            context.plusDeleteSummary();
            return;
        }
        context.plusNoopSummary();
    }

    private void markDelete(Plan plan, DestroyDataProductContext context) {
        plan.setAction(PlanAction.DELETE);
        context.plusDeleteSummary();
    }

    private void markNoop(Plan plan, DestroyDataProductContext context) {
        plan.setAction(PlanAction.NOOP);
        context.plusNoopSummary();
    }
}
