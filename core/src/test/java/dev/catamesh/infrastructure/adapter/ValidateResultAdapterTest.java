package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.PolicyLevel;
import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.model.ValidateResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidateResultAdapterTest {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";

    @Test
    void testToConsoleOutputWithNullResult() {
        Assertions.assertEquals("Validate: null", ValidateResultAdapter.toConsoleOutput(null));
    }

    @Test
    void testToConsoleOutputWithoutPolicyRules() {
        ValidateResult validateResult = new ValidateResult();

        Assertions.assertEquals("Validation passed", ValidateResultAdapter.toConsoleOutput(validateResult));
    }

    @Test
    void testToConsoleOutputWithPolicyRules() {
        ValidateResult validateResult = new ValidateResult();
        validateResult.addPolicyRule(PolicyRule.create(
                "spec.resources.bucket.definition.version",
                PolicyLevel.ERROR,
                "Published definitions are immutable. Please use a new version."
        ));

        String expected = String.join(System.lineSeparator(),
                "Policy rules:",
                RED + "! [error] spec.resources.bucket.definition.version - Published definitions are immutable. Please use a new version." + RESET
        );

        Assertions.assertEquals(expected, ValidateResultAdapter.toConsoleOutput(validateResult));
    }

    @Test
    void testToConsoleOutputNormalizesBlankPathToRoot() {
        ValidateResult validateResult = new ValidateResult();
        validateResult.addPolicyRule(PolicyRule.create("", PolicyLevel.ERROR, "Root validation failed"));

        String expected = String.join(System.lineSeparator(),
                "Policy rules:",
                RED + "! [error] <root> - Root validation failed" + RESET
        );

        Assertions.assertEquals(expected, ValidateResultAdapter.toConsoleOutput(validateResult));
    }
}
