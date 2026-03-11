package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.infrastructure.cqrs.cli.CataMeshCoreCLICommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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

    @Test
    void executeUnknownTurnsOffCliLoggingAndReturnsZero() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        Assertions.assertNotNull(rootLogger);

        Level originalRootLevel = rootLogger.getLevel();
        Map<Handler, Level> originalHandlerLevels = new IdentityHashMap<>();
        for (Handler handler : rootLogger.getHandlers()) {
            originalHandlerLevels.put(handler, handler.getLevel());
        }

        try {
            rootLogger.setLevel(Level.INFO);
            for (Handler handler : rootLogger.getHandlers()) {
                handler.setLevel(Level.INFO);
            }

            int status = CataMeshCoreCLICommand.execute(new String[]{"unknown"});

            Assertions.assertEquals(0, status);
            Assertions.assertEquals(Level.OFF, rootLogger.getLevel());
            for (Handler handler : rootLogger.getHandlers()) {
                Assertions.assertEquals(Level.OFF, handler.getLevel());
            }
        } finally {
            rootLogger.setLevel(originalRootLevel);
            for (Handler handler : rootLogger.getHandlers()) {
                handler.setLevel(originalHandlerLevels.get(handler));
            }
        }
    }
}
