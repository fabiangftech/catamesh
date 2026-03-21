package dev.catamesh.core.strategy;

import dev.catamesh.core.model.PolicyRule;

import java.util.List;

public interface PolicyRuleStrategy<I> {
    List<PolicyRule> apply(I input);
}
