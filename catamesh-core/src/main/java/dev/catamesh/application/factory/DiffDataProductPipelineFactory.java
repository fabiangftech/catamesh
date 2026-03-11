package dev.catamesh.application.factory;

import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;

public class DiffDataProductPipelineFactory implements Factory<Void, Handler<ApplyDataProductContext>> {

    private final Handler<ApplyDataProductContext> yamlToDataProductHandler;
    private final Handler<ApplyDataProductContext> validateDataProductSchemaHandler;
    private final Handler<ApplyDataProductContext> validateResourceSchemaHandler;
    private final Handler<ApplyDataProductContext> validateBucketDefinitionSchemaHandler;
    private final Handler<ApplyDataProductContext> checkIfExistDataProductHandler;
    private final Handler<ApplyDataProductContext> checkIfExistResourcesHandler;
    private final Handler<ApplyDataProductContext> planCheckResourceDefinitionVersionHandler;
    private final Handler<ApplyDataProductContext> loadCurrentDataProductForDiffHandler;
    private final Handler<ApplyDataProductContext> buildDiffV2ResultHandler;

    public DiffDataProductPipelineFactory(
            Handler<ApplyDataProductContext> yamlToDataProductHandler,
            Handler<ApplyDataProductContext> validateDataProductSchemaHandler,
            Handler<ApplyDataProductContext> validateResourceSchemaHandler,
            Handler<ApplyDataProductContext> validateBucketDefinitionSchemaHandler,
            Handler<ApplyDataProductContext> checkIfExistDataProductHandler,
            Handler<ApplyDataProductContext> checkIfExistResourcesHandler,
            Handler<ApplyDataProductContext> planCheckResourceDefinitionVersionHandler,
            Handler<ApplyDataProductContext> loadCurrentDataProductForDiffHandler,
            Handler<ApplyDataProductContext> buildDiffV2ResultHandler) {
        this.yamlToDataProductHandler = yamlToDataProductHandler;
        this.validateDataProductSchemaHandler = validateDataProductSchemaHandler;
        this.validateResourceSchemaHandler = validateResourceSchemaHandler;
        this.validateBucketDefinitionSchemaHandler = validateBucketDefinitionSchemaHandler;
        this.checkIfExistDataProductHandler = checkIfExistDataProductHandler;
        this.checkIfExistResourcesHandler = checkIfExistResourcesHandler;
        this.planCheckResourceDefinitionVersionHandler = planCheckResourceDefinitionVersionHandler;
        this.loadCurrentDataProductForDiffHandler = loadCurrentDataProductForDiffHandler;
        this.buildDiffV2ResultHandler = buildDiffV2ResultHandler;
    }

    @Override
    public Handler<ApplyDataProductContext> create(Void input) {
        yamlToDataProductHandler
                .link(validateDataProductSchemaHandler)
                .link(validateResourceSchemaHandler)
                .link(validateBucketDefinitionSchemaHandler)
                .link(checkIfExistDataProductHandler)
                .link(checkIfExistResourcesHandler)
                .link(planCheckResourceDefinitionVersionHandler)
                .link(loadCurrentDataProductForDiffHandler)
                .link(buildDiffV2ResultHandler);
        return yamlToDataProductHandler;
    }
}
