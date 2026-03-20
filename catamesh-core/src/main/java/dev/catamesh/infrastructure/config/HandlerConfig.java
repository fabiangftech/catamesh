package dev.catamesh.infrastructure.config;

import dev.catamesh.application.handler.*;
import dev.catamesh.application.strategy.ValidateImmutabilityPolicyRuleStrategy;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.ValidateDataProductContext;
import dev.catamesh.core.strategy.PolicyRuleStrategy;

public class HandlerConfig {

    public static <T> Handler<T> yamlToDataProductHandler() {
        return new YAMLToDataProductHandler<>(YAMLConfig.yamlMapper());
    }

    public static <T> Handler<T> validateImmutabilityHandler() {
        PolicyRuleStrategy<ValidateDataProductContext> immutabilityPolicyRuleStrategy =
                new ValidateImmutabilityPolicyRuleStrategy(CQRSConfig.optionalResourceDefinitionVersionQuery(DataSourceConfig.get()));
        return new ValidateImmutabilityHandler<>(immutabilityPolicyRuleStrategy);
    }

    public static <T> Handler<T> validateDataProductSchemaHandler() {
        return new ValidateDataProductSchemaHandler<>(JSONConfig.dataProductSchema(), JSONConfig.jsonMapper());
    }

    public static <T> Handler<T> validateResourceSchemaHandler() {
        return new ValidateResourceSchemaHandler<>(JSONConfig.resourceSchema(), JSONConfig.jsonMapper());
    }

    public static <T> Handler<T> validateBucketDefinitionSchemaHandler() {
        return new ValidateBucketDefinitionSchemaHandler<>(JSONConfig.bucketSchema(), JSONConfig.jsonMapper());
    }

    public static <T> Handler<T> getCurrentDataProductHandler() {
        return new GetCurrentDataProductHandler<>(CQRSConfig.optionalDataProductQuery(), CQRSConfig.allResourcesQuery(), CQRSConfig.getResourceDefinitionQuery());
    }


    public static <T> Handler<T> buildDiffDataProductHandler() {
        return new BuildDiffDataProductHandler<>();
    }

    public static <T> Handler<T> buildPlanDataProductHandler() {
        return new BuildPlanDataProductHandler<>(PlanStrategyConfig.planEngineFacade());
    }

    public static <T> Handler<T> initializeApplyDataProductHandler() {
        return new InitializeApplyDataProductHandler<>();
    }


    public static <T> Handler<T> createDataProductHandler() {
        return new CreateDataProductHandler<>(CQRSConfig.createDataProductCommand());
    }

    public static <T> Handler<T> createResourcesDataProductHandler() {
        return new CreateResourcesHandler<>(CQRSConfig.createResourceCommand());
    }


    public static <T> Handler<T> createResourcesDefinitionsHandler() {
        return new CreateResourcesDefinitionsHandler<>(CQRSConfig.createResourceDefinitionCommand());
    }

    public static <T> Handler<T> updateDataProductHandler() {
        return new UpdateDataProductHandler<>(CQRSConfig.updateDataProductCommand());
    }

    public static <T> Handler<T> updateResourceHandler() {
        return new UpdateResourceHandler<>(CQRSConfig.updateResourceCommand());
    }

    public static <T> Handler<T> buildApplyDataProductHandler() {
        return new BuildApplyDataProductHandler<>();
    }

}
