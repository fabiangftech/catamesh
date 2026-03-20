package dev.catamesh.application.factory;

import dev.catamesh.application.builder.ValidateDataProductChainFactoryBuilder;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.PlanDataProductContext;
import dev.catamesh.core.handler.ValidateDataProductContext;

import java.util.List;
import java.util.Objects;

public class ValidateDataProductChainFactory implements Factory<Void, Handler<ValidateDataProductContext>> {
    private final List<Handler<ValidateDataProductContext>> handlers;

    public ValidateDataProductChainFactory(List<Handler<ValidateDataProductContext>> handlers) {
        this.handlers = List.copyOf(Objects.requireNonNull(handlers, "handlers cannot be null"));
    }

    @Override
    public Handler<ValidateDataProductContext> create(Void input) {
        if (handlers.isEmpty()) {
            throw new IllegalStateException("At least one handler is required");
        }

        Handler<ValidateDataProductContext> first = handlers.getFirst();

        for (int i = 0; i < handlers.size() - 1; i++) {
            handlers.get(i).link(handlers.get(i + 1));
        }

        return first;
    }

    public static ValidateDataProductChainFactoryBuilder builder() {
        return new ValidateDataProductChainFactoryBuilder();
    }
}
