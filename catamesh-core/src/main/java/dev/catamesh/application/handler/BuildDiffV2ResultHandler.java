package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.v2.DiffEngine;
import dev.catamesh.infrastructure.adapter.DiffPayloadAdapter;

public class BuildDiffV2ResultHandler extends Handler<ApplyDataProductContext> {

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        context.setDiffResult(
                DiffEngine.builder().build().compare(
                        DiffPayloadAdapter.toDataProductPayload(context.getDataProduct()),
                        DiffPayloadAdapter.toDataProductPayload(context.getCurrentDataProduct())
                )
        );
    }
}
