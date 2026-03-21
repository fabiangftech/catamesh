package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.*;

public class UpdateResourceHandler<C> extends Handler<C> {
    private final Command<Resource, Resource> updateResourceCommand;

    public UpdateResourceHandler(Command<Resource, Resource> updateResourceCommand) {
        this.updateResourceCommand = updateResourceCommand;
    }

    @Override
    protected void doHandle(C context) {
        ApplyDataProductContext applyDataProductContext = (ApplyDataProductContext) context;
        PlanResult planResult = applyDataProductContext.getPlanResult();
        planResult.getSteps()
                .stream()
                .filter(step -> PlanStepType.RESOURCE.equals(step.getType()))
                .filter(step -> PlanAction.UPDATE.equals(step.getAction()))
                .forEach(step -> {
                    Resource resource = ApplyDataProductContext.resolve(applyDataProductContext, step.getPath());
                    resource.setDataProductId(Key.create(ApplyDataProductContext.resolveDataProductId(applyDataProductContext, resource)));
                    updateResourceCommand.execute(resource);
                    applyDataProductContext.markStepExecuted(step.getPath());
                });
    }
}
