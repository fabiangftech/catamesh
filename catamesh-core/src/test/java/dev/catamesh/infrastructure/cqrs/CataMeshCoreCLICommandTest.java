package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.infrastructure.cqrs.cli.CataMeshCoreCLICommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class CataMeshCoreCLICommandTest {

    @Test
    void executeDestroyPrintsPlaceholderAndReturnsZero() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int status;

        try {
            System.setOut(new PrintStream(outputStream));
            status = CataMeshCoreCLICommand.execute(new String[]{"destroy"});
        } finally {
            System.setOut(originalOut);
        }

        Assertions.assertEquals(0, status);
        Assertions.assertEquals("Not implemented destroy yet!", outputStream.toString().trim());
    }
}
