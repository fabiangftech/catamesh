package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.PlanAction;

import java.util.Optional;

public class CheckIfExistDataProductHandler extends Handler<ApplyDataProductContext> {
    private final Query<String, Optional<DataProduct>> optionalDataProductQuery;

    public CheckIfExistDataProductHandler(Query<String, Optional<DataProduct>> optionalDataProductQuery) {
        this.optionalDataProductQuery = optionalDataProductQuery;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        Optional<DataProduct> existing = optionalDataProductQuery.execute(context.getDataProductName());
        if (existing.isEmpty()) {
            context.plusCreateSummary();
            context.getPlan().setAction(PlanAction.CREATE);
            return;
        }
        context.setCurrentDataProduct(existing.get());
        context.getDataProduct().getMetadata().setId(existing.get().getMetadata().getId());
        if (DataProduct.isSame(existing.get(), context.getDataProduct())) {
            context.plusNoopSummary();
            context.getPlan().setAction(PlanAction.NOOP);
            return;
        }
        context.plusUpdateSummary();
        context.getPlan().setAction(PlanAction.UPDATE);
    }
}
