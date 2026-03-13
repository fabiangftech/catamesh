package dev.catamesh.infrastucture.cqrs.cli;

import dev.catamesh.infrastructure.cqrs.cli.CataMeshCoreCLICommand;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CataMeshCoreCLICommandTest {

    @Test
    void testDiff() {
        String yaml = new GetFileFromResourceQuery().execute("examples/data-product.example.yaml");
        String[] command = {"diff", yaml};
        Assertions.assertDoesNotThrow(()-> CataMeshCoreCLICommand.main(command));
    }
}
