package dev.catamesh.application.builder;

import dev.catamesh.application.factory.PlanDataProductChainFactory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.PlanDataProductContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlanDataProductChainFactoryBuilder {

    private final List<Handler<PlanDataProductContext>> handlers = new ArrayList<>();

    public PlanDataProductChainFactoryBuilder add(Handler<PlanDataProductContext> handler) {
        Objects.requireNonNull(handler, "handler cannot be null");
        this.handlers.add(handler);
        return this;
    }


   public PlanDataProductChainFactory build() {
       if (handlers.isEmpty()) {
           throw new IllegalStateException("At least one plan handler is required");
       }
        return new PlanDataProductChainFactory(handlers);
    }
}
