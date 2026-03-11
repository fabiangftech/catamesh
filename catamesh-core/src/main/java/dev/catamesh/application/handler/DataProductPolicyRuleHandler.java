package dev.catamesh.application.handler;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.strategy.PolicyRuleStrategy;

import java.util.List;

public class DataProductPolicyRuleHandler extends Handler<ApplyDataProductContext> {
    private final PolicyRuleStrategy<ApplyDataProductContext> immutabilityDataProductPolicyRuleStrategy;

    public DataProductPolicyRuleHandler(PolicyRuleStrategy<ApplyDataProductContext> immutabilityDataProductPolicyRuleStrategy) {
        this.immutabilityDataProductPolicyRuleStrategy = immutabilityDataProductPolicyRuleStrategy;
    }
    @Override
    protected void doHandle(ApplyDataProductContext context) {
        List<PolicyRule> policyRules = immutabilityDataProductPolicyRuleStrategy.apply(context);
        context.addPolicyRules(policyRules);
    }
}
