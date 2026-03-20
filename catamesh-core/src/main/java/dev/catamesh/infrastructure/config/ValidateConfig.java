package dev.catamesh.infrastructure.config;

import dev.catamesh.application.factory.ValidateDataProductChainFactory;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.ValidateDataProductContext;

public final class ValidateConfig {
    private ValidateConfig() {
        // do nothing
    }

    public static Factory<Void, Handler<ValidateDataProductContext>> validateDataProductChainFactory() {
        return ValidateDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.validateImmutabilityHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .build();
    }
}
