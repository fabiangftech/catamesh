package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.PlanAction;

public class UpdateDataProductHandler extends Handler<ApplyDataProductContext> {

    private final Command<DataProduct, DataProduct> updateDataProductCommand;

    public UpdateDataProductHandler(Command<DataProduct, DataProduct> updateDataProductCommand) {
        this.updateDataProductCommand = updateDataProductCommand;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        if (PlanAction.UPDATE.equals(context.getPlan().getAction())) {
            updateDataProductCommand.execute(context.getDataProduct());
        }
    }
}
