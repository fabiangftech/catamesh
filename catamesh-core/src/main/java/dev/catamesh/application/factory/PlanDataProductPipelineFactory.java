package dev.catamesh.application.factory;

import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;

public class PlanDataProductPipelineFactory implements Factory<Void, Handler<ApplyDataProductContext>> {
    private final Handler<ApplyDataProductContext> yamlToDataProductHandler;
    private final Handler<ApplyDataProductContext> validateDataProductSchemaHandler;
    private final Handler<ApplyDataProductContext> validateResourceSchemaHandler;
    private final Handler<ApplyDataProductContext> validateBucketDefinitionSchemaHandler;
    private final Handler<ApplyDataProductContext> checkIfExistDataProductHandler;
    private final Handler<ApplyDataProductContext> validateDataProductUpdateHandler;
    private final Handler<ApplyDataProductContext> checkIfExistResourcesHandler;
    private final Handler<ApplyDataProductContext> planCheckResourceDefinitionVersionHandler;
    private final Handler<ApplyDataProductContext> validateResourceDefinitionVersionImmutabilityHandler;

    public PlanDataProductPipelineFactory(
            Handler<ApplyDataProductContext> yamlToDataProductHandler,
            Handler<ApplyDataProductContext> validateDataProductSchemaHandler,
            Handler<ApplyDataProductContext> validateResourceSchemaHandler,
            Handler<ApplyDataProductContext> validateBucketDefinitionSchemaHandler,
            Handler<ApplyDataProductContext> checkIfExistDataProductHandler,
            Handler<ApplyDataProductContext> validateDataProductUpdateHandler,
            Handler<ApplyDataProductContext> checkIfExistResourcesHandler,
            Handler<ApplyDataProductContext> planCheckResourceDefinitionVersionHandler,
            Handler<ApplyDataProductContext> validateResourceDefinitionVersionImmutabilityHandler) {
        this.yamlToDataProductHandler = yamlToDataProductHandler;
        this.validateDataProductSchemaHandler = validateDataProductSchemaHandler;
        this.validateResourceSchemaHandler = validateResourceSchemaHandler;
        this.validateBucketDefinitionSchemaHandler = validateBucketDefinitionSchemaHandler;
        this.checkIfExistDataProductHandler = checkIfExistDataProductHandler;
        this.validateDataProductUpdateHandler = validateDataProductUpdateHandler;
        this.checkIfExistResourcesHandler = checkIfExistResourcesHandler;
        this.planCheckResourceDefinitionVersionHandler = planCheckResourceDefinitionVersionHandler;
        this.validateResourceDefinitionVersionImmutabilityHandler = validateResourceDefinitionVersionImmutabilityHandler;
    }

    @Override
    public Handler<ApplyDataProductContext> create(Void input) {
        yamlToDataProductHandler
                .link(validateDataProductSchemaHandler)
                .link(validateResourceSchemaHandler)
                .link(validateBucketDefinitionSchemaHandler)
                .link(checkIfExistDataProductHandler)
                .link(validateDataProductUpdateHandler)
                .link(checkIfExistResourcesHandler)
                .link(planCheckResourceDefinitionVersionHandler)
                .link(validateResourceDefinitionVersionImmutabilityHandler);
        return yamlToDataProductHandler;
    }
}
