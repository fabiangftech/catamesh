package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.infrastructure.cqrs.cli.CataMeshCoreCLICommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NewTemplateCLICommandTest {

    @Test
    void testInitDataProduct() {
        String[] command = {"new", "data-product", "my-first-data-product"};
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(command));
    }

    @Test
    void testInitDeploy() {
        String[] command = {"new", "deploy", "deploy"};
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(command));
    }

    @Test
    void testInitEnv() {
        String[] command = {"new", "env", "dev"};
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCLICommand.main(command));
    }
}
