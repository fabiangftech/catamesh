package dev.catamesh.core.handler;

import dev.catamesh.core.model.PolicyRule;
import dev.catamesh.core.model.PlanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlanDataProductContext extends DiffDataProductContext {

    private PlanResult planResult;
    private List<PolicyRule> policyRules;
    protected PlanDataProductContext(String yaml) {
        super(yaml);
    }

    public PlanResult getPlanResult() {
        return planResult;
    }

    public void setPlanResult(PlanResult planResult) {
        this.planResult = planResult;
    }

    public List<PolicyRule> getPolicyRules() {
        return Objects.isNull(policyRules) ? new ArrayList<>() : policyRules;
    }

    public void addPolicyRules(List<PolicyRule> policyRules) {
        if (Objects.isNull(this.policyRules)) {
            this.policyRules = new ArrayList<>();
        }
        this.policyRules.addAll(policyRules);
    }
}
