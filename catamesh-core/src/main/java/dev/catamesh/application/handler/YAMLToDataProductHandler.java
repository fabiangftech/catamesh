package dev.catamesh.application.handler;


import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Plan;
import dev.catamesh.infrastructure.adapter.DataProductYamlAdapter;
import tools.jackson.databind.ObjectMapper;

public class YAMLToDataProductHandler extends Handler<ApplyDataProductContext> {
    private final ObjectMapper yamlMapper;

    public YAMLToDataProductHandler( ObjectMapper yamlMapper) {
        this.yamlMapper = yamlMapper;
    }

    @Override
    protected void doHandle(ApplyDataProductContext context) {
        DataProduct dataProduct = DataProductYamlAdapter.toDataProduct(context.getYaml(), yamlMapper);
        context.setDataProduct(dataProduct);
        context.setPlan(new Plan(dataProduct.getMetadata().getName()));
    }
}
