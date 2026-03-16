package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.ApplyStep;

public class InitializeApplyDataProductHandler<C> extends Handler<C> {

    @Override
    protected void doHandle(C context) {
        ApplyDataProductContext applyDataProductContext = (ApplyDataProductContext) context;

        ApplyResult applyResult = new ApplyResult();
        applyResult.setDataProductName(applyDataProductContext.getDesiredDataProduct().getMetadata().getName());
        applyResult.setSteps(
                applyDataProductContext.getPlanResult()
                        .getSteps()
                        .stream()
                        .map(ApplyStep::from)
                        .toList()
        );

        applyDataProductContext.setApplyResult(applyResult);
    }
}
