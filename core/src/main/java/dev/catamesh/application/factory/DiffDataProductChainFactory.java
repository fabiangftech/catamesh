package dev.catamesh.application.factory;

import dev.catamesh.application.builder.DiffDataProductChainFactoryBuilder;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.DiffDataProductContext;
import dev.catamesh.core.handler.Handler;

import java.util.List;
import java.util.Objects;

public class DiffDataProductChainFactory implements Factory<Void, Handler<DiffDataProductContext>> {

    private final List<Handler<DiffDataProductContext>> handlers;

    public DiffDataProductChainFactory(List<Handler<DiffDataProductContext>> handlers) {
        this.handlers = List.copyOf(Objects.requireNonNull(handlers, "handlers cannot be null"));
    }

    @Override
    public Handler<DiffDataProductContext> create(Void input) {
        if (handlers.isEmpty()) {
            throw new IllegalStateException("At least one handler is required");
        }

        Handler<DiffDataProductContext> first = handlers.getFirst();

        for (int i = 0; i < handlers.size() - 1; i++) {
            handlers.get(i).link(handlers.get(i + 1));
        }

        return first;
    }

    public static DiffDataProductChainFactoryBuilder builder() {
        return new DiffDataProductChainFactoryBuilder();
    }
}
