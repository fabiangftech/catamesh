package dev.catamesh.application.factory;

import dev.catamesh.application.builder.ApplyDataProductChainFactoryBuilder;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;

import java.util.List;
import java.util.Objects;

public class ApplyDataProductChainFactory implements Factory<Void, Handler<ApplyDataProductContext>> {
    private final List<Handler<ApplyDataProductContext>> handlers;

    public ApplyDataProductChainFactory(List<Handler<ApplyDataProductContext>> handlers) {
        this.handlers = List.copyOf(Objects.requireNonNull(handlers, "handlers cannot be null"));
    }

    @Override
    public Handler<ApplyDataProductContext> create(Void input) {
        if (handlers.isEmpty()) {
            throw new IllegalStateException("At least one handler is required");
        }

        Handler<ApplyDataProductContext> first = handlers.getFirst();

        for (int i = 0; i < handlers.size() - 1; i++) {
            handlers.get(i).link(handlers.get(i + 1));
        }

        return first;
    }

    public static ApplyDataProductChainFactoryBuilder builder() {
        return new ApplyDataProductChainFactoryBuilder();
    }
}