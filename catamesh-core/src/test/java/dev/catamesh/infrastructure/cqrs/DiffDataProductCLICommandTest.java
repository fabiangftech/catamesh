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

class DiffDataProductCLICommandTest {

    private final ApplicationConfig applicationConfig = new ApplicationConfig();
    private final Query<String, String> getFileFromResourceQuery = applicationConfig.getFileFromResourceQuery();

    @Test
    void testPlanDataProduct() {
        String yaml = getFileFromResourceQuery.execute("examples/data-product.example.yaml");
        String[] command = {"apply", yaml};
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(command));

        String yamlTwo = getFileFromResourceQuery.execute("examples/data-product-2.example.yaml");
        String[] commandTwo = {"diff", yamlTwo};
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(commandTwo));

    }

    @Test
    void testDiffPrintsV2DiffPayload() throws Exception {
        String uniqueName = "diff-cli-" + UUID.randomUUID();
        String yaml = getFileFromResourceQuery.execute("examples/data-product.example.yaml")
                .replace("my-first-data-product", uniqueName)
                .replace("displayName: Test", "displayName: " + uniqueName);
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(new String[]{"apply", yaml}));

        String yamlTwo = getFileFromResourceQuery.execute("examples/data-product-2.example.yaml")
                .replace("my-first-data-product", uniqueName);
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(outputStream));
            Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(new String[]{"diff", yamlTwo}));
        } finally {
            System.setOut(originalOut);
        }

        JsonNode result = applicationConfig.jsonMapper().readTree(outputStream.toString().trim());
        Assertions.assertNotNull(result.get("root"));
        Assertions.assertNotNull(result.get("summary"));
        Assertions.assertNotNull(result.get("root").get("entries").get("spec"));
        Assertions.assertEquals(40, result.get("root")
                .get("entries")
                .get("spec")
                .get("entries")
                .get("resources")
                .get("entries")
                .get("my-first-component")
                .get("entries")
                .get("definition")
                .get("entries")
                .get("config")
                .get("entries")
                .get("lifecycleDays")
                .get("newValue")
                .asInt());
        Assertions.assertTrue(result.get("summary").get("changed").asInt() > 0);
    }
}
