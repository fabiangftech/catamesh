package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanStepType;

public class UpdateDataProductHandler<C> extends Handler<C> {
    private final Command<DataProduct, DataProduct> updateDataProductCommand;

    public UpdateDataProductHandler(Command<DataProduct, DataProduct> updateDataProductCommand) {
        this.updateDataProductCommand = updateDataProductCommand;
    }
    @Override
    protected void doHandle(C context) {
        ApplyDataProductContext applyDataProductContext = (ApplyDataProductContext) context;
        boolean shouldCreate = applyDataProductContext.getPlanResult().getSteps().stream()
                .anyMatch(step ->
                        step.getAction() == PlanAction.UPDATE &&
                        (step.getType() == PlanStepType.METADATA || step.getType() == PlanStepType.SPEC)
                );
        if (shouldCreate) {
            updateDataProductCommand.execute(applyDataProductContext.getDesiredDataProduct());
            applyDataProductContext.getPlanResult().getSteps().stream()
                    .filter(step -> step.getAction() == PlanAction.UPDATE)
                    .filter(step -> step.getType() == PlanStepType.METADATA || step.getType() == PlanStepType.SPEC)
                    .forEach(step -> applyDataProductContext.markStepExecuted(step.getPath()));
        }
    }
}
