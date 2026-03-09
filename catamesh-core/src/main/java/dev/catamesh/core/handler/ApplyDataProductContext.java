package dev.catamesh.core.handler;

import dev.catamesh.core.model.*;

import java.util.ArrayList;
import java.util.List;

public class ApplyDataProductContext {
    private final String yaml;
    private DataProduct dataProduct;
    private DataProduct currentDataProduct;
    private Plan plan;
    private Diff diff;
    private List<DiffSection> diffSections;
    private DiffSummary diffSummary;

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

    public Diff getDiffResult() {
        return diff;
    }

    public void setDiffResult(Diff diff) {
        this.diff = diff;
    }

    public List<DiffSection> getDiffSections() {
        return diffSections;
    }

    public void setDiffSections(List<DiffSection> diffSections) {
        this.diffSections = diffSections;
    }

    public void addDiffSection(DiffSection diffSection) {
        if (diffSections == null) {
            diffSections = new ArrayList<>();
        }
        diffSections.add(diffSection);
    }

    public DiffSummary getDiffSummary() {
        return diffSummary;
    }

    public void setDiffSummary(DiffSummary diffSummary) {
        this.diffSummary = diffSummary;
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


    public static ApplyDataProductContext create(String yaml) {
        return new ApplyDataProductContext(yaml);
    }
}
