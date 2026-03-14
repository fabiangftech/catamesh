package dev.catamesh.application.factory;

import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;

public class ApplyDataProductChainFactory implements Factory<Void, Handler<ApplyDataProductContext>> {
    private final Handler<ApplyDataProductContext> yamlToDataProductHandler;
    private final Handler<ApplyDataProductContext> validateDataProductSchemaHandler;
    private final Handler<ApplyDataProductContext> validateResourceSchemaHandler;
    private final Handler<ApplyDataProductContext> validateBucketDefinitionSchemaHandler;
    private final Handler<ApplyDataProductContext> getCurrentDataProductHandler;
    private final Handler<ApplyDataProductContext> buildDiffDataProductHandler;
    private final Handler<ApplyDataProductContext> planDataProductPolicyRuleHandler;
    private final Handler<ApplyDataProductContext> buildPlanDataProductHandler;
    private final Handler<ApplyDataProductContext> createDataProductHandler;
    private final Handler<ApplyDataProductContext> createResourcesHandler;

    public ApplyDataProductChainFactory(Handler<ApplyDataProductContext> yamlToDataProductHandler,
                                        Handler<ApplyDataProductContext> validateDataProductSchemaHandler,
                                        Handler<ApplyDataProductContext> validateResourceSchemaHandler,
                                        Handler<ApplyDataProductContext> validateBucketDefinitionSchemaHandler,
                                        Handler<ApplyDataProductContext> getCurrentDataProductHandler,
                                        Handler<ApplyDataProductContext> buildDiffDataProductHandler,
                                        Handler<ApplyDataProductContext> planDataProductPolicyRuleHandler,
                                        Handler<ApplyDataProductContext> buildPlanDataProductHandler,
                                        Handler<ApplyDataProductContext> createDataProductHandler,
                                        Handler<ApplyDataProductContext> createResourcesHandler
    ) {
        this.yamlToDataProductHandler = yamlToDataProductHandler;
        this.validateDataProductSchemaHandler = validateDataProductSchemaHandler;
        this.validateResourceSchemaHandler = validateResourceSchemaHandler;
        this.validateBucketDefinitionSchemaHandler = validateBucketDefinitionSchemaHandler;
        this.getCurrentDataProductHandler = getCurrentDataProductHandler;
        this.buildDiffDataProductHandler = buildDiffDataProductHandler;
        this.planDataProductPolicyRuleHandler = planDataProductPolicyRuleHandler;
        this.buildPlanDataProductHandler = buildPlanDataProductHandler;
        this.createDataProductHandler = createDataProductHandler;
        this.createResourcesHandler = createResourcesHandler;
    }

    @Override
    public Handler<ApplyDataProductContext> create(Void input) {
        this.yamlToDataProductHandler
                .link(validateDataProductSchemaHandler)
                .link(validateResourceSchemaHandler)
                .link(validateBucketDefinitionSchemaHandler)
                .link(getCurrentDataProductHandler)
                .link(buildDiffDataProductHandler)
                .link(planDataProductPolicyRuleHandler)
                .link(buildPlanDataProductHandler)
                .link(createDataProductHandler)
                .link(createResourcesHandler);
        return yamlToDataProductHandler;
    }
}
