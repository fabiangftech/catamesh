package dev.catamesh.infrastructure.cqrs.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CataMeshCoreCliCommandTest {

    @Test
    void testInitDataProduct() {
        String[] command = {"init", "data-product", "my-first-data-product"};
        Assertions.assertDoesNotThrow(() -> CataMeshCoreCliCommand.main(command));
    }
}
