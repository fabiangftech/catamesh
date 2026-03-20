package dev.catamesh.application.factory;

import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.ValidateDataProductContext;

public class ValidateDataProductContextChainFactory implements Factory<Void, Handler<ValidateDataProductContext>> {
    @Override
    public Handler<ValidateDataProductContext> create(Void input) {
        return null;
    }
}
