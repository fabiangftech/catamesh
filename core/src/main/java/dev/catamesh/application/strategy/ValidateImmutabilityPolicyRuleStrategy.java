package dev.catamesh.application.strategy;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.ValidateDataProductContext;
import dev.catamesh.core.model.PolicyLevel;
import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.core.strategy.PolicyRuleStrategy;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ValidateImmutabilityPolicyRuleStrategy implements PolicyRuleStrategy<ValidateDataProductContext> {

    private final Query<GetResourceDefinitionDTO, Optional<ResourceDefinition>> optionalResourceDefinitionVersionQuery;

    public ValidateImmutabilityPolicyRuleStrategy(Query<GetResourceDefinitionDTO, Optional<ResourceDefinition>> optionalResourceDefinitionVersionQuery) {
        this.optionalResourceDefinitionVersionQuery = optionalResourceDefinitionVersionQuery;
    }

    @Override
    public List<PolicyRule> apply(ValidateDataProductContext context) {
        List<PolicyRule> policyRules = new ArrayList<>();
        context.getDesiredDataProduct().getSpec().getResources().forEach(resource -> {
            Optional<ResourceDefinition> optional
                    = optionalResourceDefinitionVersionQuery.execute(GetResourceDefinitionDTO.create(resource.getId(), resource.getDefinition().getVersion()));
            if (optional.isPresent() && !ResourceDefinition.isSameVersionContent(optional.get(), resource.getDefinition())) {
                String path = "spec.resources." + resource.getName() + ".definition.version";
                String message = String.format(
                        "Resource definition version '%s' already exists for resource '%s'. " +
                        "Published definitions are immutable. Please use a new version.",
                        resource.getDefinition().getVersion(),
                        resource.getName()
                );
                policyRules.add(PolicyRule.create(path, PolicyLevel.ERROR, message));
            }
        });
        return policyRules;
    }
}
