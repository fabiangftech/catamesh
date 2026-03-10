package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.PlanAction;

public class ValidateDataProductUpdateHandler extends Handler<ApplyDataProductContext> {

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        if (!PlanAction.UPDATE.equals(context.getPlan().getAction())) {
            return;
        }

        if (context.getCurrentDataProduct() == null) {
            return;
        }

        DataProduct.validateMutableUpdate(
                context.getCurrentDataProduct(),
                context.getDataProduct()
        );
    }
}
