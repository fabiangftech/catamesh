package dev.catamesh.infrastructure.adapter;

import dev.catamesh.core.model.DataProduct;
import dev.catamesh.infrastructure.config.YAMLConfig;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DataProductYamlAdapterTest {

    private final GetFileFromResourceQuery getFileFromResourceQuery = new GetFileFromResourceQuery();
    private final YAMLConfig yamlConfig = new YAMLConfig();

    @Test
    void toDataProductParsesExampleYaml() {
        String yaml = getFileFromResourceQuery.execute("examples/data-product.example.yaml");

        DataProduct dataProduct = DataProductYamlAdapter.toDataProduct(yaml, yamlConfig.yamlMapper());

        Assertions.assertEquals("my-first-data-product", dataProduct.getMetadata().getName());
        Assertions.assertEquals("data-product/v1", dataProduct.getSchemaVersion().getValue());
        Assertions.assertEquals(1, dataProduct.getSpec().getResources().size());
        Assertions.assertEquals("my-first-component", dataProduct.getSpec().getResources().get(0).getName());
    }
}
