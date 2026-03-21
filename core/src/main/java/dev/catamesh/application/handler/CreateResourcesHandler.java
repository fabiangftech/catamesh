package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.core.model.PlanStepType;
import dev.catamesh.core.model.Resource;

public class CreateResourcesHandler<C> extends Handler<C> {

    private final Command<Resource, Void> createResourceCommand;

    public CreateResourcesHandler(Command<Resource, Void> createResourceCommand) {
        this.createResourceCommand = createResourceCommand;
    }

    @Override
    protected void doHandle(C context) {
        ApplyDataProductContext applyDataProductContext = (ApplyDataProductContext) context;
        PlanResult planResult = applyDataProductContext.getPlanResult();
        planResult.getSteps()
                .stream()
                .filter(step -> PlanStepType.RESOURCE.equals(step.getType()))
                .filter(step -> PlanAction.CREATE.equals(step.getAction()))
                .forEach(step -> {
                    Resource resource = ApplyDataProductContext.resolve(applyDataProductContext, step.getPath());
                    resource.setDataProductId(Key.create(ApplyDataProductContext.resolveDataProductId(applyDataProductContext, resource)));
                    createResourceCommand.execute(resource);
                    applyDataProductContext.markStepExecuted(step.getPath());
                });
    }
}
