package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DiffEngine;
import dev.catamesh.infrastructure.adapter.DiffPayloadAdapter;

public class BuildDiffResultHandler extends Handler<ApplyDataProductContext> {

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        context.setDiffResult(
                DiffEngine.builder()
                        .exclude("metadata.id")
                        .exclude("metadata.name")
                        .exclude("spec.resources[?].id")
                        .exclude("spec.resources[?].dataProductId")
                        .build().compare(
                        DiffPayloadAdapter.toDataProductPayload(context.getDataProduct()),
                        DiffPayloadAdapter.toDataProductPayload(context.getCurrentDataProduct())
                )
        );
    }
}
