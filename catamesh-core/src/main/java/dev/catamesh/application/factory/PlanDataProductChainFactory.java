package dev.catamesh.application.factory;

import dev.catamesh.application.builder.DiffDataProductChainFactoryBuilder;
import dev.catamesh.application.builder.PlanDataProductChainFactoryBuilder;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.DiffDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.PlanDataProductContext;

import java.util.List;
import java.util.Objects;

public class PlanDataProductChainFactory implements Factory<Void, Handler<PlanDataProductContext>> {
    private final List<Handler<PlanDataProductContext>> handlers;

    public PlanDataProductChainFactory(List<Handler<PlanDataProductContext>> handlers) {
        this.handlers = List.copyOf(Objects.requireNonNull(handlers, "handlers cannot be null"));
    }

    @Override
    public Handler<PlanDataProductContext> create(Void input) {
        if (handlers.isEmpty()) {
            throw new IllegalStateException("At least one handler is required");
        }

        Handler<PlanDataProductContext> first = handlers.getFirst();

        for (int i = 0; i < handlers.size() - 1; i++) {
            handlers.get(i).link(handlers.get(i + 1));
        }

        return first;
    }

    public static PlanDataProductChainFactoryBuilder builder() {
        return new PlanDataProductChainFactoryBuilder();
    }
}