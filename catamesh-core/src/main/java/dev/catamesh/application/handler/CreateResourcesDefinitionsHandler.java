package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.*;

public class CreateResourcesDefinitionsHandler<Context> extends Handler<Context> {

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
                    //TODO FALTA HACER EL MATCH ENTRE EL STEP Y EL RESOURCE DEFINITION
                    applyDataProductContext.getDesiredResources()
                            .forEach(resource -> {
                        createResourceDefinitionCommand.execute(resource);
                    });
                });
    }
}
