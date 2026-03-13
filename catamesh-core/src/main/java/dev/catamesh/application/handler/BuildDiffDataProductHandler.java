package dev.catamesh.application.handler;

import dev.catamesh.core.handler.v2.DiffDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DiffEngine;
import dev.catamesh.infrastructure.adapter.DiffPayloadAdapter;

public class BuildDiffDataProductHandler<Context> extends Handler<Context> {
    @Override
    protected void doHandle(Context context) {
        DiffDataProductContext diffDataProductContext = (DiffDataProductContext) context;
        diffDataProductContext.setDiffResult(
                DiffEngine.builder()
                        .exclude("metadata.id")
                        .exclude("metadata.name")
                        .exclude("spec.resources[?].id")
                        .exclude("spec.resources[?].dataProductId")
                        .build().compare(
                                DiffPayloadAdapter.toDataProductPayload(diffDataProductContext.getDesiredDataProduct()),
                                DiffPayloadAdapter.toDataProductPayload(diffDataProductContext.getCurrentDataProduct())
                        )
        );
    }
}
