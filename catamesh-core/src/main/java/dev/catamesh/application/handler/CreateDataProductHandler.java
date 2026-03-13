package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.PlanAction;
import dev.catamesh.core.model.PlanStepType;

public class CreateDataProductHandler<Context> extends Handler<Context> {
    private final Command<DataProduct, DataProduct> createDataProductCommand;

    public CreateDataProductHandler(Command<DataProduct, DataProduct> createDataProductCommand) {
        this.createDataProductCommand = createDataProductCommand;
    }

    @Override
    protected void doHandle(Context context) {
        ApplyDataProductContext applyDataProductContext = (ApplyDataProductContext) context;
        boolean shouldCreate = applyDataProductContext.getPlanResult().getSteps().stream()
                .anyMatch(step ->
                        step.getAction() == PlanAction.CREATE &&
                        (step.getType() == PlanStepType.METADATA || step.getType() == PlanStepType.SPEC)
                );
        if (shouldCreate) {
            createDataProductCommand.execute(applyDataProductContext.getDesiredDataProduct());
        }
    }
}
