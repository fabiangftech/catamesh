package dev.catamesh.application.factory;


import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;

public class ApplyDestroyDataProductPipelineFactory implements Factory<Void, Handler<DestroyDataProductContext>> {
    private final Handler<DestroyDataProductContext> yamlToDestroyDataProductHandler;
    private final Handler<DestroyDataProductContext> validateDestroyDataProductSchemaHandler;
    private final Handler<DestroyDataProductContext> validateDestroyResourceSchemaHandler;
    private final Handler<DestroyDataProductContext> validateDestroyBucketDefinitionSchemaHandler;
    private final Handler<DestroyDataProductContext> validateDestroyDefinitionVersionHandler;
    private final Handler<DestroyDataProductContext> getOptionalDataProductForDestroyHandler;
    private final Handler<DestroyDataProductContext> getResourcesForDestroyHandler;
    private final Handler<DestroyDataProductContext> planDestroyDataProductHandler;
    private final Handler<DestroyDataProductContext> destroyDataProductHandler;
    private final Handler<DestroyDataProductContext> destroyResourceHandler;
    private final Handler<DestroyDataProductContext> destroyResourceDefinitionHandler;

    public ApplyDestroyDataProductPipelineFactory(Handler<DestroyDataProductContext> yamlToDestroyDataProductHandler,
                                                  Handler<DestroyDataProductContext> validateDestroyDataProductSchemaHandler,
                                                  Handler<DestroyDataProductContext> validateDestroyResourceSchemaHandler,
                                                  Handler<DestroyDataProductContext> validateDestroyBucketDefinitionSchemaHandler,
                                                  Handler<DestroyDataProductContext> validateDestroyDefinitionVersionHandler,
                                                  Handler<DestroyDataProductContext> getOptionalDataProductForDestroyHandler,
                                                  Handler<DestroyDataProductContext> getResourcesForDestroyHandler,
                                                  Handler<DestroyDataProductContext> planDestroyDataProductHandler,
                                                  Handler<DestroyDataProductContext> destroyDataProductHandler,
                                                  Handler<DestroyDataProductContext> destroyResourceHandler,
                                                  Handler<DestroyDataProductContext> destroyResourceDefinitionHandler) {
        this.yamlToDestroyDataProductHandler = yamlToDestroyDataProductHandler;
        this.validateDestroyDataProductSchemaHandler = validateDestroyDataProductSchemaHandler;
        this.validateDestroyResourceSchemaHandler = validateDestroyResourceSchemaHandler;
        this.validateDestroyBucketDefinitionSchemaHandler = validateDestroyBucketDefinitionSchemaHandler;
        this.validateDestroyDefinitionVersionHandler = validateDestroyDefinitionVersionHandler;
        this.getOptionalDataProductForDestroyHandler = getOptionalDataProductForDestroyHandler;
        this.getResourcesForDestroyHandler = getResourcesForDestroyHandler;
        this.planDestroyDataProductHandler = planDestroyDataProductHandler;
        this.destroyDataProductHandler = destroyDataProductHandler;
        this.destroyResourceHandler = destroyResourceHandler;
        this.destroyResourceDefinitionHandler = destroyResourceDefinitionHandler;
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
                .link(destroyResourceDefinitionHandler)
                .link(destroyResourceHandler)
                .link(destroyDataProductHandler);
        return yamlToDestroyDataProductHandler;
    }
}
