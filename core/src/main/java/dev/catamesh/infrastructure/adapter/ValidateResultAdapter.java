package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.model.ValidateResult;

import java.util.ArrayList;
import java.util.List;

public final class ValidateResultAdapter {

    private ValidateResultAdapter() {
        // do nothing
    }

    public static String toConsoleOutput(ValidateResult validateResult) {
        if (validateResult == null) {
            return "Validate: null";
        }

        List<PolicyRule> policyRules = validateResult.getPolicyRules() == null ? List.of() : validateResult.getPolicyRules();
        if (policyRules.isEmpty()) {
            return "Validation passed";
        }

        List<String> lines = new ArrayList<>();
        lines.add("Policy rules:");
        for (PolicyRule policyRule : policyRules) {
            if (policyRule != null) {
                lines.add(ConsoleFormatterAdapter.formatPolicyRule(policyRule));
            }
        }

        return String.join(System.lineSeparator(), lines);
    }
}
