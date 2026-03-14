package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.core.model.PlanStepType;
import dev.catamesh.core.model.Resource;

public class CreateResourcesDefinitionsHandler<Context> extends Handler<Context> {
    private static final String RESOURCE_DEFINITION_SUFFIX = ".definition";

    private final Command<Resource, Resource> createResourceDefinitionCommand;

    public CreateResourcesDefinitionsHandler(Command<Resource, Resource> createResourceDefinitionCommand) {
        this.createResourceDefinitionCommand = createResourceDefinitionCommand;
    }

    @Override
    protected void doHandle(Context context) {
        ApplyDataProductContext applyDataProductContext = (ApplyDataProductContext) context;
        PlanResult planResult = applyDataProductContext.getPlanResult();
        planResult.getSteps()
                .stream()
                .filter(step -> PlanStepType.RESOURCE_DEFINITION.equals(step.getType()))
                .filter(step -> PlanAction.CREATE.equals(step.getAction()))
                .forEach(step -> {
                    Resource resource = applyDataProductContext.getDesiredResources()
                            .stream()
                            .filter(candidate -> resourceDefinitionPath(candidate).equals(step.getPath()))
                            .findFirst()
                            .orElseThrow(() -> new InvariantException(
                                    String.format("Resource definition path=%s was not found in desired data product", step.getPath())
                            ));

                    if (resource.getId() == null) {
                        throw new InvariantException(
                                String.format(
                                        "Resource id is required to save definition for resource=%s. Resource must be created before its definition.",
                                        resource.getName()
                                )
                        );
                    }

                    createResourceDefinitionCommand.execute(resource);
                    applyDataProductContext.markStepExecuted(step.getPath());
                });
    }

    private String resourceDefinitionPath(Resource resource) {
        return resource.getResourcePath() + RESOURCE_DEFINITION_SUFFIX;
    }
}
