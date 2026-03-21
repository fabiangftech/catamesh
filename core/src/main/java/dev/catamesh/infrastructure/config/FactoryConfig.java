package dev.catamesh.infrastructure.config;

import dev.catamesh.application.factory.ApplyDataProductChainFactory;
import dev.catamesh.application.factory.DiffDataProductChainFactory;
import dev.catamesh.application.factory.PlanDataProductChainFactory;
import dev.catamesh.application.factory.ValidateDataProductChainFactory;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.*;

public final class FactoryConfig {
    private FactoryConfig() {
        // do nothing
    }
    public static Factory<Void, Handler<ValidateDataProductContext>> validateDataProductChainFactory() {
        return ValidateDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.getCurrentDataProductHandler())
                .add(HandlerConfig.validateImmutabilityHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .build();
    }
    public static Factory<Void, Handler<DiffDataProductContext>> diffDataProductChainFactory() {
        return DiffDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.getCurrentDataProductHandler())
                .add(HandlerConfig.validateImmutabilityHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .add(HandlerConfig.buildDiffDataProductHandler())
                .build();

    }
    public static Factory<Void, Handler<PlanDataProductContext>> planDataProductChainFactory() {
        return PlanDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.getCurrentDataProductHandler())
                .add(HandlerConfig.validateImmutabilityHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .add(HandlerConfig.buildDiffDataProductHandler())
                .add(HandlerConfig.buildPlanDataProductHandler())
                .build();
    }
    public static Factory<Void, Handler<ApplyDataProductContext>> applyDataProductChainFactory() {
        return ApplyDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.getCurrentDataProductHandler())
                .add(HandlerConfig.validateImmutabilityHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .add(HandlerConfig.buildDiffDataProductHandler())
                .add(HandlerConfig.buildPlanDataProductHandler())
                .add(HandlerConfig.initializeApplyDataProductHandler())
                .add(HandlerConfig.createDataProductHandler())
                .add(HandlerConfig.createResourcesDataProductHandler())
                .add(HandlerConfig.createResourcesDefinitionsHandler())
                .add(HandlerConfig.updateDataProductHandler())
                .add(HandlerConfig.updateResourceHandler())
                .add(HandlerConfig.buildApplyDataProductHandler())
                .build();
    }
}
