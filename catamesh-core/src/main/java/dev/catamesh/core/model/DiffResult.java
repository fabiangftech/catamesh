package dev.catamesh.core.model;

import java.util.List;

public final class DiffResult {

    private final DiffTreeNode root;
    private List<PolicyRule> policyRules;

    public DiffResult(DiffTreeNode root) {
        this.root = root;
    }

    public DiffTreeNode getRoot() {
        return root;
    }

    public DiffSummary getSummary() {
        return root.computeSummary();
    }

    public boolean hasChanges() {
        return getSummary().totalChanges() > 0;
    }

    public List<PolicyRule> getPolicyRules() {
        return policyRules;
    }

    public void setPolicyRules(List<PolicyRule> policyRules) {
        this.policyRules = policyRules;
    }
}