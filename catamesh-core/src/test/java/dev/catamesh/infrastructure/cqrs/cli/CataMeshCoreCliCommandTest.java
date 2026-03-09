package dev.catamesh.infrastructure.cqrs.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CataMeshCoreCliCommandTest {

    @Test
    void testInitDataProduct() {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
        String[] command = {"init", "data-product", "my-first-data-product"};

        int exitCode = CataMeshCoreCliCommand.execute(
                command,
                new PrintStream(stdout),
                new PrintStream(stderr)
        );

        Assertions.assertEquals(0, exitCode);
        Assertions.assertTrue(stdout.toString().contains("schemaVersion: data-product/v1"));
        Assertions.assertTrue(stdout.toString().contains("name: my-first-data-product"));
        Assertions.assertFalse(stdout.toString().contains("option no valid"));
        Assertions.assertEquals("", stderr.toString());
    }

    @Test
    void testInitDataProductRequiresName() {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        int exitCode = CataMeshCoreCliCommand.execute(
                new String[]{"init", "data-product"},
                new PrintStream(stdout),
                new PrintStream(stderr)
        );

        Assertions.assertEquals(1, exitCode);
        Assertions.assertEquals("", stdout.toString());
        Assertions.assertTrue(stderr.toString().contains("Missing data-product name."));
    }

    @Test
    void testInvalidCommandReturnsFailure() {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        int exitCode = CataMeshCoreCliCommand.execute(
                new String[]{"unknown"},
                new PrintStream(stdout),
                new PrintStream(stderr)
        );

        Assertions.assertEquals(1, exitCode);
        Assertions.assertEquals("", stdout.toString());
        Assertions.assertTrue(stderr.toString().contains("Invalid command: unknown"));
        Assertions.assertTrue(stderr.toString().contains("Usage: cata <command> [args]"));
    }
}
