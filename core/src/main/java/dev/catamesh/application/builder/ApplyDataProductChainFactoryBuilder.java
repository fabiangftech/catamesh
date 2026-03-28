package dev.catamesh.application.builder;

import dev.catamesh.application.factory.ApplyDataProductChainFactory;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplyDataProductChainFactoryBuilder {

    private final List<Handler<ApplyDataProductContext>> handlers = new ArrayList<>();

    public ApplyDataProductChainFactoryBuilder add(Handler<ApplyDataProductContext> handler) {
        Objects.requireNonNull(handler, "handler apply cannot be null");
        this.handlers.add(handler);
        return this;
    }


   public ApplyDataProductChainFactory build() {
       if (handlers.isEmpty()) {
           throw new IllegalStateException("At least one apply handler is required");
       }
        return new ApplyDataProductChainFactory(handlers);
    }
}
