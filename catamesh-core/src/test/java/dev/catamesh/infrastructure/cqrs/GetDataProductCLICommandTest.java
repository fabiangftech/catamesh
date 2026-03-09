package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.infrastructure.config.ApplicationConfig;
import dev.catamesh.infrastructure.cqrs.cli.CataMeshCoreCLICommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GetDataProductCLICommandTest {

    private final ApplicationConfig applicationConfig = new ApplicationConfig();
    private final Query<String, String> getFileFromResourceQuery = applicationConfig.getFileFromResourceQuery();

    @Test
    void testPlanDataProduct() {
        String yaml = getFileFromResourceQuery.execute("examples/data-product.example.yaml");
        String[] command = {"apply", yaml};
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(command));
System.out.println("--------------------------------");
        String dataProductName = "my-first-data-product";
        String[] commandTwo = {"get", "data-product", dataProductName};
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(commandTwo));

    }
}
