package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.PolicyLevel;
import dev.catamesh.core.model.PolicyRule;

final class ConsoleFormatterAdapter {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";

    private ConsoleFormatterAdapter() {
        // utility class
    }

    static String formatPolicyRule(PolicyRule policyRule) {
        String level = normalizeLevel(policyRule.level());
        String path = normalizePath(policyRule.path());
        String message = String.valueOf(policyRule.message());
        String line = "! [" + level + "] " + path + " - " + message;

        if (policyRule.level() == PolicyLevel.ERROR) {
            return RED + line + RESET;
        }

        return line;
    }

    private static String normalizePath(String path) {
        if (path == null || path.isBlank()) {
            return "<root>";
        }
        return path;
    }

    private static String normalizeLevel(PolicyLevel level) {
        if (level == null) {
            return "unknown";
        }
        return level.getValue();
    }
}
