package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.PlanResult;
import dev.catamesh.core.model.PolicyLevel;
import dev.catamesh.core.model.PolicyRule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class PlanToStringAdapterTest {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";

    @Test
    void testToStringFormatsPolicyRulesConsistently() {
        PlanResult planResult = new PlanResult();
        planResult.setPolicyRules(List.of(PolicyRule.create("", PolicyLevel.ERROR, "Root validation failed")));

        String expected = String.join(System.lineSeparator(),
                "No changes",
                "",
                "Policy rules:",
                RED + "! [error] <root> - Root validation failed" + RESET,
                "",
                CYAN + "Summary: 0 create, 0 update, 0 delete, 0 noop" + RESET
        );

        Assertions.assertEquals(expected, PlanToStringAdapter.toString(planResult));
    }
}
