package dev.catamesh.application.handler;

import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.ValidateDataProductContext;
import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.strategy.PolicyRuleStrategy;

import java.util.List;

public class ValidateImmutabilityHandler<C> extends Handler<C> {
    private final PolicyRuleStrategy<ValidateDataProductContext> immutabilityPolicyRuleStrategy;

    public ValidateImmutabilityHandler(PolicyRuleStrategy<ValidateDataProductContext> immutabilityPolicyRuleStrategy) {
        this.immutabilityPolicyRuleStrategy = immutabilityPolicyRuleStrategy;
    }

    @Override
    protected void doHandle(C context) {
        ValidateDataProductContext validateDataProductContext = (ValidateDataProductContext) context;
        List<PolicyRule> immutabilityPolicyRules = immutabilityPolicyRuleStrategy.apply(validateDataProductContext);
        immutabilityPolicyRules.forEach(validateDataProductContext::addPolicyRule);
    }
}
