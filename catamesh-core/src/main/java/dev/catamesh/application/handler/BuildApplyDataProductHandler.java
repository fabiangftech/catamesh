package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.ApplyStatus;

import java.time.LocalDateTime;

public class BuildApplyDataProductHandler<Context> extends Handler<Context> {

    @Override
    protected void doHandle(Context context) {
        ApplyDataProductContext applyDataProductContext = (ApplyDataProductContext) context;
        ApplyResult applyResult = new ApplyResult();
        applyResult.setDataProductName(applyDataProductContext.getDesiredDataProduct().getMetadata().getName());
        applyResult.setStatus(ApplyStatus.SUCCESS);
        applyResult.setSteps(null);
        applyResult.setSummary(null);
        applyResult.setExecutedAt(LocalDateTime.now());
        applyDataProductContext.setApplyResult(applyResult);
    }
}
