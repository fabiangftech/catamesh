package dev.catamesh.application.handler;

import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.PlanDataProductContext;
import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.strategy.PolicyRuleStrategy;

import java.util.List;

public class PlanDataProductPolicyRuleHandler<C> extends Handler<C> {
    private final PolicyRuleStrategy<PlanDataProductContext> immutabilityPolicyRuleStrategy;

    public PlanDataProductPolicyRuleHandler(PolicyRuleStrategy<PlanDataProductContext> immutabilityPolicyRuleStrategy) {
        this.immutabilityPolicyRuleStrategy = immutabilityPolicyRuleStrategy;
    }
    @Override
    protected void doHandle(C context) {
        PlanDataProductContext planDataProductContext = (PlanDataProductContext) context;
        List<PolicyRule> immutabilityPolicyRules = immutabilityPolicyRuleStrategy.apply(planDataProductContext);
        planDataProductContext.addPolicyRules(immutabilityPolicyRules);
    }
}
