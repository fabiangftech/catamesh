package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.model.ValidateResult;

public final class ValidateResultAdapter {

    private ValidateResultAdapter() {
        // do nothing
    }

    public static String toConsoleOutput(ValidateResult validateResult) {
        StringBuilder consoleOutput = new StringBuilder();
        for (PolicyRule policyRule : validateResult.getPolicyRules()) {
            consoleOutput
                    .append(policyRule.path())
                    .append(" : ")
                    .append(policyRule.message())
                    .append("\n");
        }
        return consoleOutput.toString();
    }
}
