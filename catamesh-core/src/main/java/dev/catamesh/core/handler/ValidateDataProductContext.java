package dev.catamesh.core.handler;

import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.model.ValidateResult;

import java.util.Objects;

public class ValidateDataProductContext extends DataProductContext {

    private ValidateResult validateResult;

    protected ValidateDataProductContext(String yaml) {
        super(yaml);
    }

    public ValidateResult getValidateResult() {
        return validateResult;
    }

    public void addPolicyRule(PolicyRule policyRule) {
        if (Objects.isNull(validateResult)) {
            this.validateResult = new ValidateResult();
        }
        this.validateResult.addPolicyRule(policyRule);
    }
}
