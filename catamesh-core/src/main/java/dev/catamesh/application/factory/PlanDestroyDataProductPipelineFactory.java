package dev.catamesh.application.factory;


import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;

public class PlanDestroyDataProductPipelineFactory implements Factory<Void, Handler<DestroyDataProductContext>> {

    private final Handler<DestroyDataProductContext> yamlToDestroyDataProductHandler;
    private final Handler<DestroyDataProductContext> validateDestroyDataProductSchemaHandler;
    private final Handler<DestroyDataProductContext> validateDestroyResourceSchemaHandler;
    private final Handler<DestroyDataProductContext> validateDestroyBucketDefinitionSchemaHandler;
    private final Handler<DestroyDataProductContext> validateDestroyDefinitionVersionHandler;
    private final Handler<DestroyDataProductContext> getOptionalDataProductForDestroyHandler;
    private final Handler<DestroyDataProductContext> getResourcesForDestroyHandler;
    private final Handler<DestroyDataProductContext> planDestroyDataProductHandler;
    private final Handler<DestroyDataProductContext> planDestroyTerminalHandler;

    public PlanDestroyDataProductPipelineFactory(
            Handler<DestroyDataProductContext> yamlToDestroyDataProductHandler,
            Handler<DestroyDataProductContext> validateDestroyDataProductSchemaHandler,
            Handler<DestroyDataProductContext> validateDestroyResourceSchemaHandler,
            Handler<DestroyDataProductContext> validateDestroyBucketDefinitionSchemaHandler,
            Handler<DestroyDataProductContext> validateDestroyDefinitionVersionHandler,
            Handler<DestroyDataProductContext> getOptionalDataProductForDestroyHandler,
            Handler<DestroyDataProductContext> getResourcesForDestroyHandler,
            Handler<DestroyDataProductContext> planDestroyDataProductHandler,
            Handler<DestroyDataProductContext> planDestroyTerminalHandler) {
        this.yamlToDestroyDataProductHandler = yamlToDestroyDataProductHandler;
        this.validateDestroyDataProductSchemaHandler = validateDestroyDataProductSchemaHandler;
        this.validateDestroyResourceSchemaHandler = validateDestroyResourceSchemaHandler;
        this.validateDestroyBucketDefinitionSchemaHandler = validateDestroyBucketDefinitionSchemaHandler;
        this.validateDestroyDefinitionVersionHandler = validateDestroyDefinitionVersionHandler;
        this.getOptionalDataProductForDestroyHandler = getOptionalDataProductForDestroyHandler;
        this.getResourcesForDestroyHandler = getResourcesForDestroyHandler;
        this.planDestroyDataProductHandler = planDestroyDataProductHandler;
        this.planDestroyTerminalHandler = planDestroyTerminalHandler;
    }

    @Override
    public Handler<DestroyDataProductContext> create(Void input) {
        yamlToDestroyDataProductHandler
                .link(validateDestroyDataProductSchemaHandler)
                .link(validateDestroyResourceSchemaHandler)
                .link(validateDestroyBucketDefinitionSchemaHandler)
                .link(validateDestroyDefinitionVersionHandler)
                .link(getOptionalDataProductForDestroyHandler)
                .link(getResourcesForDestroyHandler)
                .link(planDestroyDataProductHandler)
                .link(planDestroyTerminalHandler);
        return yamlToDestroyDataProductHandler;
    }
}
