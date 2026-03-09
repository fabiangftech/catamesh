package dev.catamesh.application.handler;

import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;
import tools.jackson.databind.ObjectMapper;

public class YAMLToDestroyDataProductHandler extends Handler<DestroyDataProductContext> {

    private final ObjectMapper yamlMapper;

    public YAMLToDestroyDataProductHandler(ObjectMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        DataProduct requestDataProduct = yamlMapper.readValue(context.getYaml(), DataProduct.class);
        context.setRequestDataProduct(requestDataProduct);
    }
}
