package dev.catamesh.application.handler;

import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.DataProductContext;
import dev.catamesh.core.model.DataProduct;
import tools.jackson.databind.ObjectMapper;

public class YAMLToDataProductHandler<C> extends Handler<C> {
    private final ObjectMapper yamlMapper;
    public YAMLToDataProductHandler(ObjectMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    @Override
    protected void doHandle(C context) {
        DataProductContext dataProductContext = (DataProductContext) context;
        DataProduct dataProduct = yamlMapper.readValue(dataProductContext.getYaml(), DataProduct.class);
        dataProductContext.setDesiredDataProduct(dataProduct);
    }
}
