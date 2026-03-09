package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.infrastructure.config.ApplicationConfig;
import dev.catamesh.infrastructure.cqrs.cli.DataProductCLICommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NewDataProductCLICommandTest {

    private final ApplicationConfig applicationConfig = new ApplicationConfig();
    private final Query<String, String> getFileFromResourceQuery = applicationConfig.getFileFromResourceQuery();

    @Test
    void testPlanDataProduct() {
        String yaml = getFileFromResourceQuery.execute("examples/data-product.example.yaml");
        String[] command = {"plan", yaml};
        Assertions.assertDoesNotThrow(() -> DataProductCLICommand.main(command));
    }
}
