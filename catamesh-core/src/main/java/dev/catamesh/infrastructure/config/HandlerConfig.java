package dev.catamesh.infrastructure.config;

import dev.catamesh.application.handler.ValidateBucketDefinitionSchemaHandler;
import dev.catamesh.application.handler.ValidateDataProductSchemaHandler;
import dev.catamesh.application.handler.ValidateResourceSchemaHandler;
import dev.catamesh.application.handler.YAMLToDataProductHandler;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.PlanDataProductContext;

public class HandlerConfig {

    public static <T> Handler<T> yamlToDataProductHandler() {
        return new YAMLToDataProductHandler<>(YAMLConfig.yamlMapper());
    }

    public static <T> Handler<T> validateDataProductSchemaHandler() {
        return new ValidateDataProductSchemaHandler<>(JSONConfig.resourceSchema(), JSONConfig.jsonMapper());
    }

    public static <T> Handler<T> validateResourceSchemaHandler() {
        return new ValidateResourceSchemaHandler<>(JSONConfig.resourceSchema(), JSONConfig.jsonMapper());
    }

    public static <T> Handler<T> validateBucketDefinitionSchemaHandler() {
        return new ValidateBucketDefinitionSchemaHandler<>(JSONConfig.bucketSchema(), JSONConfig.jsonMapper());
    }
}
