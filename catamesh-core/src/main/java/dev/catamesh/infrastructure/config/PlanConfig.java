package dev.catamesh.infrastructure.config;

import dev.catamesh.application.factory.PlanDataProductChainFactory;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.PlanDataProductContext;

public final class PlanConfig {

    private  PlanConfig() {
        // do nothing
    }

    public static Factory<Void, Handler<PlanDataProductContext>> planDataProductChainFactory() {
        return PlanDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .add(HandlerConfig.getCurrentDataProductHandler())
                .add(HandlerConfig.buildDiffDataProductHandler())
                .add(HandlerConfig.buildPlanDataProductHandler())
                .build();
    }
}
