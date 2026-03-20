package dev.catamesh.infrastructure.config;

import dev.catamesh.application.factory.ValidateDataProductChainFactory;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.ValidateDataProductContext;

import javax.sql.DataSource;

public class ValidateConfig {
    public static Factory<Void, Handler<ValidateDataProductContext>> validateDataProductChainFactory(DataSource dataSource) {
        return ValidateDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .build();
    }
}
