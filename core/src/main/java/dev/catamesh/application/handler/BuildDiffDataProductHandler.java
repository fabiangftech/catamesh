package dev.catamesh.application.handler;

import dev.catamesh.core.handler.DiffDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.application.facade.DiffEngineFacade;
import dev.catamesh.infrastructure.adapter.DiffPayloadAdapter;

public class BuildDiffDataProductHandler<C> extends Handler<C> {
    @Override
    protected void doHandle(C context) {
        DiffDataProductContext diffDataProductContext = (DiffDataProductContext) context;
        diffDataProductContext.setDiffResult(
                DiffEngineFacade.builder()
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
