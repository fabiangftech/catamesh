package dev.catamesh.application.handler;

import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.v2.DataProductContext;
import dev.catamesh.core.model.DataProduct;
import tools.jackson.databind.ObjectMapper;

public class YAMLToDataProductHandler<Context> extends Handler<Context> {
    private final ObjectMapper yamlMapper;
    public YAMLToDataProductHandler(ObjectMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    @Override
    protected void doHandle(Context context) {
        DataProductContext dataProductContext = (DataProductContext) context;
        DataProduct dataProduct = yamlMapper.readValue(dataProductContext.getYaml(), DataProduct.class);
        dataProductContext.setDesiredDataProduct(dataProduct);
    }
}
