package dev.catamesh.core.handler;

import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Resource;

import java.util.List;

public abstract class DataProductContext {
    private final String yaml;
    private DataProduct desiredDataProduct;
    private DataProduct currentDataProduct;

    protected DataProductContext(String yaml) {
        this.yaml = yaml;
    }

    public String getYaml() {
        return yaml;
    }

    public DataProduct getDesiredDataProduct() {
        return desiredDataProduct;
    }

    public void setDesiredDataProduct(DataProduct desiredDataProduct) {
        this.desiredDataProduct = desiredDataProduct;
    }

    public List<Resource> getDesiredResources() {
        return desiredDataProduct.getSpec().getResources();
    }

    public DataProduct getCurrentDataProduct() {
        return currentDataProduct;
    }

    public void setCurrentDataProduct(DataProduct currentDataProduct) {
        this.currentDataProduct = currentDataProduct;
    }
}
