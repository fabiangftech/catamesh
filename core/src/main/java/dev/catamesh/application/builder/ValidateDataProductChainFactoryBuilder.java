package dev.catamesh.application.builder;

import dev.catamesh.application.factory.ValidateDataProductChainFactory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.ValidateDataProductContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ValidateDataProductChainFactoryBuilder {

    private final List<Handler<ValidateDataProductContext>> handlers = new ArrayList<>();

    public ValidateDataProductChainFactoryBuilder add(Handler<ValidateDataProductContext> handler) {
        Objects.requireNonNull(handler, "handler cannot be null");
        this.handlers.add(handler);
        return this;
    }


    public ValidateDataProductChainFactory build() {
        if (handlers.isEmpty()) {
            throw new IllegalStateException("At least one validate handler is required");
        }
        return new ValidateDataProductChainFactory(handlers);
    }
}
