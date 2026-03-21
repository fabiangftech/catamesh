package dev.catamesh.core.model;

import java.util.ArrayList;
import java.util.List;

public class ValidateResult {
    private final List<PolicyRule> policyRules;

    public ValidateResult() {
        this.policyRules = new ArrayList<>();
    }

    public List<PolicyRule> getPolicyRules() {
        return policyRules;
    }

    public void addPolicyRule(PolicyRule policyRule) {
        this.policyRules.add(policyRule);
    }
}
