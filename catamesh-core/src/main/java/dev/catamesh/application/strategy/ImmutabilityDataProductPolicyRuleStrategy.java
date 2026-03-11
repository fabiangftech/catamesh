package dev.catamesh.application.strategy;

import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.strategy.PolicyRuleStrategy;

import java.util.List;
import java.util.Optional;

public class ImmutabilityDataProductPolicyRuleStrategy implements PolicyRuleStrategy<ApplyDataProductContext> {

    public ImmutabilityDataProductPolicyRuleStrategy() {

    }
    @Override
    public List<PolicyRule> apply(ApplyDataProductContext context) {
        // get all resource and definition active from DB
        context.getDataProduct().getSpec().getResources().forEach(resource -> {
            resource.getDefinition().getVersion();
            //todo check if definition version exist in db
            context.addPolicyRule(null);

        });
        return null;
    }
}
