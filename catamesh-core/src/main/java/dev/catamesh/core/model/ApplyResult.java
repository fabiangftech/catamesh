package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplyResult {
    private Plan plan;
    private DataProduct dataProduct;

    public ApplyResult() {
        // do nothing
    }

    public ApplyResult(Plan plan, DataProduct dataProduct) {
        this.plan = plan;
        this.dataProduct = dataProduct;
    }

    public Plan getPlan() {
        return plan;
    }

    public DataProduct getDataProduct() {
        return dataProduct;
    }
}
