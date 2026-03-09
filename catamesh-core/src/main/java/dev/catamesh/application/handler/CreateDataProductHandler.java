package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.PlanAction;

public class CreateDataProductHandler extends Handler<ApplyDataProductContext> {

    private final Command<DataProduct, DataProduct> createDataProductCommand;

    public CreateDataProductHandler(Command<DataProduct, DataProduct> createDataProductCommand) {
        this.createDataProductCommand = createDataProductCommand;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        if (context.getPlan().getAction().equals(PlanAction.CREATE)) {
            createDataProductCommand.execute(context.getDataProduct());
        }
    }
}
