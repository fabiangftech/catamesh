package dev.catamesh.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplyResult {
    private DataProduct dataProduct;

    public ApplyResult() {
        // do nothing
    }

    public ApplyResult( DataProduct dataProduct) {
        this.dataProduct = dataProduct;
    }

    public DataProduct getDataProduct() {
        return dataProduct;
    }
}
