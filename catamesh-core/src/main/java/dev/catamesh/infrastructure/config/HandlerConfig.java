package dev.catamesh.infrastructure.config;

import dev.catamesh.application.handler.*;
import dev.catamesh.application.strategy.ValidateImmutabilityPolicyRuleStrategy;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.ValidateDataProductContext;
import dev.catamesh.core.strategy.PolicyRuleStrategy;

public class HandlerConfig {

    public static <T> Handler<T> yamlToDataProductHandler() {
        return new YAMLToDataProductHandler<>(YAMLConfig.yamlMapper());
    }

    public static <T> Handler<T> validateImmutabilityHandler() {
        PolicyRuleStrategy<ValidateDataProductContext> immutabilityPolicyRuleStrategy =
                new ValidateImmutabilityPolicyRuleStrategy(CQRSConfig.optionalResourceDefinitionVersionQuery(DataSourceConfig.dataSource()));
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
}
