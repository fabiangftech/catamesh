package dev.catamesh.application.factory;


import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;

public class ApplyDataProductPipelineFactory implements Factory<Void, Handler<ApplyDataProductContext>> {
    private final Handler<ApplyDataProductContext> yamlToDataProductHandler;
    private final Handler<ApplyDataProductContext> validateDataProductSchemaHandler;
    private final Handler<ApplyDataProductContext> validateResourceSchemaHandler;
    private final Handler<ApplyDataProductContext> validateBucketDefinitionSchemaHandler;
    private final Handler<ApplyDataProductContext> checkIfExistDataProductHandler;
    private final Handler<ApplyDataProductContext> createDataProductHandler;
    private final Handler<ApplyDataProductContext> checkIfExistResourcesHandler;
    private final Handler<ApplyDataProductContext> createResourcesHandler;
    private final Handler<ApplyDataProductContext> checkIfExistResourceDefinitionVersionHandler;
    private final Handler<ApplyDataProductContext> createResourceDefinitionsHandler;

    public ApplyDataProductPipelineFactory(
            Handler<ApplyDataProductContext> yamlToDataProductHandler,
            Handler<ApplyDataProductContext> validateDataProductSchemaHandler,
            Handler<ApplyDataProductContext> validateResourceSchemaHandler,
            Handler<ApplyDataProductContext> validateBucketDefinitionSchemaHandler,
            Handler<ApplyDataProductContext> checkIfExistDataProductHandler,
            Handler<ApplyDataProductContext> createDataProductHandler,
            Handler<ApplyDataProductContext> checkIfExistResourcesHandler,
            Handler<ApplyDataProductContext> createResourcesHandler,
            Handler<ApplyDataProductContext> checkIfExistResourceDefinitionVersionHandler,
            Handler<ApplyDataProductContext> createResourceDefinitionsHandler) {
        this.yamlToDataProductHandler = yamlToDataProductHandler;
        this.validateDataProductSchemaHandler = validateDataProductSchemaHandler;
        this.validateResourceSchemaHandler = validateResourceSchemaHandler;
        this.validateBucketDefinitionSchemaHandler = validateBucketDefinitionSchemaHandler;
        this.checkIfExistDataProductHandler = checkIfExistDataProductHandler;
        this.createDataProductHandler = createDataProductHandler;
        this.checkIfExistResourcesHandler = checkIfExistResourcesHandler;
        this.createResourcesHandler = createResourcesHandler;
        this.checkIfExistResourceDefinitionVersionHandler = checkIfExistResourceDefinitionVersionHandler;
        this.createResourceDefinitionsHandler = createResourceDefinitionsHandler;
    }

    @Override
    public Handler<ApplyDataProductContext> create(Void input) {
        yamlToDataProductHandler
                .link(validateDataProductSchemaHandler)
                .link(validateResourceSchemaHandler)
                .link(validateBucketDefinitionSchemaHandler)
                .link(checkIfExistDataProductHandler)
                .link(createDataProductHandler)
                .link(checkIfExistResourcesHandler)
                .link(createResourcesHandler)
                .link(checkIfExistResourceDefinitionVersionHandler)
                .link(createResourceDefinitionsHandler);
        return yamlToDataProductHandler;
    }
}
