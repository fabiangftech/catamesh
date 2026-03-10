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

    @Test
    void testApplyImmutableDefinitionErrorPrintsStructuredError() throws Exception {
        String uniqueName = "apply-cli-error-" + UUID.randomUUID();
        String yaml = getFileFromResourceQuery.execute("examples/data-product.example.yaml")
                .replace("my-first-data-product", uniqueName)
                .replace("displayName: Test", "displayName: " + uniqueName);
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(new String[]{"apply", yaml}));

        String changedYaml = yaml.replace("lifecycleDays: 30", "lifecycleDays: 99");
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
        int status;

        try {
            System.setOut(new PrintStream(outputStream));
            System.setErr(new PrintStream(errorStream));
            status = CataMeshCoreCLICommand.execute(new String[]{"apply", changedYaml});
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }

        Assertions.assertEquals(21, status);
        Assertions.assertEquals("", outputStream.toString().trim());

        JsonNode error = applicationConfig.jsonMapper().readTree(errorStream.toString().trim());
        Assertions.assertEquals("CONFLICT_ERROR", error.get("errorCode").asText());
        Assertions.assertEquals(21, error.get("status").asInt());
        Assertions.assertEquals("Definition version is immutable", error.get("title").asText());
        Assertions.assertTrue(error.get("message").asText().contains("my-first-component"));
        Assertions.assertEquals("config", error.get("details").get(0).asText());
    }
}
