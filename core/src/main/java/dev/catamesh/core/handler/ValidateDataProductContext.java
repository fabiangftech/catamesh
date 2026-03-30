package dev.catamesh.core.handler;

import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.model.ValidateResult;

import java.util.Objects;

public class ValidateDataProductContext extends DataProductContext {

    private final ValidateResult validateResult;

    protected ValidateDataProductContext(String yaml) {
        super(yaml);
        this.validateResult = new ValidateResult();
    }

    public ValidateResult getValidateResult() {
        return Objects.isNull(validateResult) ? new ValidateResult() : this.validateResult;
    }

    public void addPolicyRule(PolicyRule policyRule) {
        this.validateResult.addPolicyRule(policyRule);
    }

    public static ValidateDataProductContext create(String yaml) {
        return new ValidateDataProductContext(yaml);
    }
}
