package dev.catamesh.application.factory;

import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.DiffDataProductContext;

public class DiffDataProductChainFactory implements Factory<Void, Handler<DiffDataProductContext>> {
    private final Handler<DiffDataProductContext> yamlToDataProductHandler;
    private final Handler<DiffDataProductContext> validateDataProductSchemaHandler;
    private final Handler<DiffDataProductContext> validateResourceSchemaHandler;
    private final Handler<DiffDataProductContext> validateBucketDefinitionSchemaHandler;
    private final Handler<DiffDataProductContext> getCurrentDataProductHandler;
    private final Handler<DiffDataProductContext> buildDiffDataProductHandler;

    public DiffDataProductChainFactory(Handler<DiffDataProductContext> yamlToDataProductHandler,
                                       Handler<DiffDataProductContext> validateDataProductSchemaHandler,
                                       Handler<DiffDataProductContext> validateResourceSchemaHandler,
                                       Handler<DiffDataProductContext> validateBucketDefinitionSchemaHandler,
                                       Handler<DiffDataProductContext> getCurrentDataProductHandler,
                                       Handler<DiffDataProductContext> buildDiffDataProductHandler) {
        this.yamlToDataProductHandler = yamlToDataProductHandler;
        this.validateDataProductSchemaHandler = validateDataProductSchemaHandler;
        this.validateResourceSchemaHandler = validateResourceSchemaHandler;
        this.validateBucketDefinitionSchemaHandler = validateBucketDefinitionSchemaHandler;
        this.getCurrentDataProductHandler = getCurrentDataProductHandler;
        this.buildDiffDataProductHandler = buildDiffDataProductHandler;
    }

    @Override
    public Handler<DiffDataProductContext> create(Void input) {
        this.yamlToDataProductHandler
                .link(validateDataProductSchemaHandler)
                .link(validateResourceSchemaHandler)
                .link(validateBucketDefinitionSchemaHandler)
                .link(getCurrentDataProductHandler)
                .link(buildDiffDataProductHandler);
        return yamlToDataProductHandler;
    }
}
