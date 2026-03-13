package dev.catamesh.core.handler;


import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Resource;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DestroyDataProductContext {

    private final String yaml;
    private final DestroyMode mode;
    private DataProduct requestDataProduct;
    private DataProduct dataProduct;

    public DestroyDataProductContext(String yaml, DestroyMode mode) {
        this.yaml = yaml;
        this.mode = mode;
    }

    public String getYaml() {
        return yaml;
    }

    public DestroyMode getMode() {
        return mode;
    }


    public String getName() {
        if (Objects.isNull(requestDataProduct)
            || Objects.isNull(requestDataProduct.getMetadata())) {
            return null;
        }
        return requestDataProduct.getMetadata().getName();
    }

    public DataProduct getRequestDataProduct() {
        return requestDataProduct;
    }

    public void setRequestDataProduct(DataProduct requestDataProduct) {
        this.requestDataProduct = requestDataProduct;
    }

    public List<Resource> getRequestedResources() {
        if (Objects.isNull(requestDataProduct)
            || Objects.isNull(requestDataProduct.getSpec())
            || Objects.isNull(requestDataProduct.getSpec().getResources())) {
            return Collections.emptyList();
        }
        return requestDataProduct.getSpec().getResources();
    }

    public DataProduct getDataProduct() {
        return dataProduct;
    }

    public void setDataProduct(DataProduct dataProduct) {
        this.dataProduct = dataProduct;
    }


    public List<Resource> getResources() {
        if (Objects.isNull(dataProduct)
            || Objects.isNull(dataProduct.getSpec())
            || Objects.isNull(dataProduct.getSpec().getResources())) {
            return Collections.emptyList();
        }
        return dataProduct.getSpec().getResources();
    }

    public static DestroyDataProductContext createForPlan(String yaml) {
        return new DestroyDataProductContext(yaml, DestroyMode.PLAN);
    }

    public static DestroyDataProductContext createForApply(String yaml) {
        return new DestroyDataProductContext(yaml, DestroyMode.APPLY);
    }
}
