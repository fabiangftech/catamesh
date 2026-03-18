package dev.catamesh.application.builder;

import dev.catamesh.application.factory.DiffDataProductChainFactory;
import dev.catamesh.core.handler.DiffDataProductContext;
import dev.catamesh.core.handler.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiffDataProductChainFactoryBuilder {

    private final List<Handler<DiffDataProductContext>> handlers = new ArrayList<>();

    public DiffDataProductChainFactoryBuilder add(Handler<DiffDataProductContext> handler) {
        Objects.requireNonNull(handler, "handler cannot be null");
        this.handlers.add(handler);
        return this;
    }

    public DiffDataProductChainFactory build() {
        if (handlers.isEmpty()) {
            throw new IllegalStateException("At least one diff handler is required");
        }
        return new DiffDataProductChainFactory(List.copyOf(handlers));
    }
}
