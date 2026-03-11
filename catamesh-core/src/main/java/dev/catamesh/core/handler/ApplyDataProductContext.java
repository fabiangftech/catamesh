package dev.catamesh.core.handler;

import dev.catamesh.core.model.*;
import dev.catamesh.core.model.DiffResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplyDataProductContext {
    private final String yaml;
    private DataProduct dataProduct;
    private DataProduct currentDataProduct;
    private Plan plan;
    private DiffResult diffResult;
    private List<PolicyRule> policyRules;

    public ApplyDataProductContext(String yaml) {
        this.yaml = yaml;
    }

    public String getYaml() {
        return yaml;
    }

    public DataProduct getDataProduct() {
        return dataProduct;
    }

    public Key getDataProductId() {
        return Key.create(this.getDataProduct().getMetadata().getId());
    }

    public List<Resource> getResources() {
        return dataProduct.getSpec().getResources();
    }

    public String getDataProductName() {
        return dataProduct.getMetadata().getName();
    }

    public void setDataProduct(DataProduct dataProduct) {
        this.dataProduct = dataProduct;
    }

    public DataProduct getCurrentDataProduct() {
        return currentDataProduct;
    }

    public void setCurrentDataProduct(DataProduct currentDataProduct) {
        this.currentDataProduct = currentDataProduct;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public DiffResult getDiffResult() {
        return diffResult;
    }

    public void setDiffResult(DiffResult diffResult) {
        this.diffResult = diffResult;
    }

    public void plusCreateSummary() {
        this.plan.plusCreateSummary();
    }

    public void plusUpdateSummary() {
        this.plan.plusUpdateSummary();
    }

    public void plusNoopSummary() {
        this.plan.plusNoopSummary();
    }

    public List<PolicyRule> getPolicyRules() {
        return Objects.isNull(policyRules) ? new ArrayList<>() : policyRules;
    }

    public void addPolicyRule(PolicyRule policyRule) {
        if (Objects.isNull(policyRules)) {
            this.policyRules = new ArrayList<>();
        }
        this.policyRules.add(policyRule);
    }

    public void addPolicyRules(List<PolicyRule> policyRules) {
        if (Objects.isNull(this.policyRules)) {
            this.policyRules = new ArrayList<>();
        }
        this.policyRules.addAll(policyRules);
    }


    public static ApplyDataProductContext create(String yaml) {
        return new ApplyDataProductContext(yaml);
    }
}
