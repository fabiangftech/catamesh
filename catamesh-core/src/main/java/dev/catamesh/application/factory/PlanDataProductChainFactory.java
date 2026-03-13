package dev.catamesh.application.factory;

import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.v2.DiffDataProductContext;
import dev.catamesh.core.handler.v2.PlanDataProductContext;

public class PlanDataProductChainFactory implements Factory<Void, Handler<PlanDataProductContext>> {
    private final Handler<PlanDataProductContext> yamlToDataProductHandler;
    private final Handler<PlanDataProductContext> validateDataProductSchemaHandler;
    private final Handler<PlanDataProductContext> validateResourceSchemaHandler;
    private final Handler<PlanDataProductContext> validateBucketDefinitionSchemaHandler;
    private final Handler<PlanDataProductContext> getCurrentDataProductHandler;
    private final Handler<PlanDataProductContext> buildDiffDataProductHandler;
    private final Handler<PlanDataProductContext> planDataProductPolicyRuleHandler;
    private final Handler<PlanDataProductContext> buildPlanDataProductHandler;

    public PlanDataProductChainFactory(Handler<PlanDataProductContext> yamlToDataProductHandler,
                                       Handler<PlanDataProductContext> validateDataProductSchemaHandler,
                                       Handler<PlanDataProductContext> validateResourceSchemaHandler,
                                       Handler<PlanDataProductContext> validateBucketDefinitionSchemaHandler,
                                       Handler<PlanDataProductContext> getCurrentDataProductHandler,
                                       Handler<PlanDataProductContext> buildDiffDataProductHandler,
                                       Handler<PlanDataProductContext> planDataProductPolicyRuleHandler,
                                       Handler<PlanDataProductContext> buildPlanDataProductHandler
    ) {
        this.yamlToDataProductHandler = yamlToDataProductHandler;
        this.validateDataProductSchemaHandler = validateDataProductSchemaHandler;
        this.validateResourceSchemaHandler = validateResourceSchemaHandler;
        this.validateBucketDefinitionSchemaHandler = validateBucketDefinitionSchemaHandler;
        this.getCurrentDataProductHandler = getCurrentDataProductHandler;
        this.buildDiffDataProductHandler = buildDiffDataProductHandler;
        this.planDataProductPolicyRuleHandler = planDataProductPolicyRuleHandler;
        this.buildPlanDataProductHandler = buildPlanDataProductHandler;
    }

    @Override
    public Handler<PlanDataProductContext> create(Void input) {
        this.yamlToDataProductHandler
                .link(validateDataProductSchemaHandler)
                .link(validateResourceSchemaHandler)
                .link(validateBucketDefinitionSchemaHandler)
                .link(getCurrentDataProductHandler)
                .link(buildDiffDataProductHandler)
                .link(planDataProductPolicyRuleHandler)
                .link(buildPlanDataProductHandler);
        return yamlToDataProductHandler;
    }
}