package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;

public class UpdateResourceDefinitionHandler<C> extends Handler<C> {
    @Override
    protected void doHandle(C context) {
        ApplyDataProductContext applyDataProductContext = (ApplyDataProductContext) context;
    }
}
