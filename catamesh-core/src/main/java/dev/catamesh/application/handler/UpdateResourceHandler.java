package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.Resource;

public class UpdateResourceHandler<C> extends Handler<C> {
    private final Command<Resource, Resource> updateResourceCommand;

    public UpdateResourceHandler(Command<Resource, Resource> updateResourceCommand) {
        this.updateResourceCommand = updateResourceCommand;
    }

    @Override
    protected void doHandle(C context) {
        ApplyDataProductContext applyDataProductContext = (ApplyDataProductContext) context;
        //todo impl
    }
}
