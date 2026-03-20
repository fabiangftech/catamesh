package dev.catamesh.infrastructure.config;

import dev.catamesh.application.factory.DiffDataProductChainFactory;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.DiffDataProductContext;

public final class DiffConfig {

    private DiffConfig() {
        // do nothing
    }

    public static Factory<Void, Handler<DiffDataProductContext>> diffDataProductChainFactory() {
        return DiffDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.validateImmutabilityHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .add(HandlerConfig.getCurrentDataProductHandler())
                .add(HandlerConfig.buildDiffDataProductHandler())
                .build();

    }
}
