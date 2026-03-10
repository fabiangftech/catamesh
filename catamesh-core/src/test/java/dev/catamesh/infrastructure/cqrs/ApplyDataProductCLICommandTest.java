package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.infrastructure.config.ApplicationConfig;
import dev.catamesh.infrastructure.cqrs.cli.CataMeshCoreCLICommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;

class ApplyDataProductCLICommandTest {

    private final ApplicationConfig applicationConfig = new ApplicationConfig();
    private final Query<String, String> getFileFromResourceQuery = applicationConfig.getFileFromResourceQuery();

    @Test
    void testPlanDataProduct() {
        String yaml = getFileFromResourceQuery.execute("examples/data-product.example.yaml");
        String[] command = {"apply", yaml};
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(command));
    }

    @Test
    void testApplyPrintsJsonApplyResult() throws Exception {
        String uniqueName = "apply-cli-" + UUID.randomUUID();
        String yaml = getFileFromResourceQuery.execute("examples/data-product.example.yaml")
                .replace("my-first-data-product", uniqueName)
                .replace("displayName: Test", "displayName: " + uniqueName);
        String[] command = {"apply", yaml};
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(outputStream));
            Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(command));
        } finally {
            System.setOut(originalOut);
        }

        String json = outputStream.toString().trim();
        JsonNode result = applicationConfig.jsonMapper().readTree(json);
        Assertions.assertEquals(uniqueName, result.get("dataProduct").get("metadata").get("name").asText());
        Assertions.assertNotNull(result.get("plan"));
        Assertions.assertNotNull(result.get("dataProduct"));
    }
}
