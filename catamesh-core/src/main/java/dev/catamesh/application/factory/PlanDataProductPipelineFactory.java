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
    private final Handler<ApplyDataProductContext> checkIfExistResourcesHandler;
    private final Handler<ApplyDataProductContext> planCheckResourceDefinitionVersionHandler;

    public PlanDataProductPipelineFactory(
            Handler<ApplyDataProductContext> yamlToDataProductHandler,
            Handler<ApplyDataProductContext> validateDataProductSchemaHandler,
            Handler<ApplyDataProductContext> validateResourceSchemaHandler,
            Handler<ApplyDataProductContext> validateBucketDefinitionSchemaHandler,
            Handler<ApplyDataProductContext> checkIfExistDataProductHandler,
            Handler<ApplyDataProductContext> checkIfExistResourcesHandler,
            Handler<ApplyDataProductContext> planCheckResourceDefinitionVersionHandler) {
        this.yamlToDataProductHandler = yamlToDataProductHandler;
        this.validateDataProductSchemaHandler = validateDataProductSchemaHandler;
        this.validateResourceSchemaHandler = validateResourceSchemaHandler;
        this.validateBucketDefinitionSchemaHandler = validateBucketDefinitionSchemaHandler;
        this.checkIfExistDataProductHandler = checkIfExistDataProductHandler;
        this.checkIfExistResourcesHandler = checkIfExistResourcesHandler;
        this.planCheckResourceDefinitionVersionHandler = planCheckResourceDefinitionVersionHandler;
    }

    @Override
    public Handler<ApplyDataProductContext> create(Void input) {
        yamlToDataProductHandler
                .link(validateDataProductSchemaHandler)
                .link(validateResourceSchemaHandler)
                .link(validateBucketDefinitionSchemaHandler)
                .link(checkIfExistDataProductHandler)
                .link(checkIfExistResourcesHandler)
                .link(planCheckResourceDefinitionVersionHandler);
        return yamlToDataProductHandler;
    }
}
