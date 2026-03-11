package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.core.model.ModelType;
import dev.catamesh.infrastructure.adapter.CLIErrorAdapter;
import dev.catamesh.infrastructure.adapter.CLIJsonAdapter;
import dev.catamesh.infrastructure.dto.CLIErrorPayloadDTO;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@SuppressWarnings("java:S106")
public class CataMeshCoreCLICommand {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final Map<String, Consumer<String[]>> COMMAND_HANDLERS = Map.of(
            "new", NewTemplateCLICommand::main,
            "plan", CataMeshCoreCLICommand::runDataProductCommand,
            "apply", CataMeshCoreCLICommand::runDataProductCommand,
            "get", CataMeshCoreCLICommand::get,
            "diff", CataMeshCoreCLICommand::runDataProductCommand,
            "destroy", CataMeshCoreCLICommand::destroy
    );

    public static void main(String[] command) {
        int status = execute(command);
        if (status != 0) {
            System.exit(status);
        }
    }

    public static int execute(String[] command) {
        configureCliLogging();
        try {
            dispatch(command[0], command);
            return 0;
        } catch (Throwable error) {
            CLIErrorPayloadDTO payload = CLIErrorAdapter.map(error);
            writeError(payload);
            return payload.getStatus();
        }
    }

    private static void dispatch(String verb, String[] command) {
        Consumer<String[]> handler = COMMAND_HANDLERS.get(verb);
        if (handler == null) {
            return;
        }
        handler.accept(command);
    }

    private static void runDataProductCommand(String[] command) {
        //todo check if schema is data-product/v1
        DataProductCLICommand.main(command);
    }

    private static void get(String[] command) {
        String type = command[1]; // data-product/deploy/env
        if (!ModelType.DATA_PRODUCT.getValue().equals(type)) {
            return;
        }
        DataProductCLICommand.main(command);
    }

    private static void destroy(String[] command) {
        System.out.println("Not implemented destroy yet!");
    }

    private static void configureCliLogging() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        if (rootLogger == null) {
            return;
        }

        rootLogger.setLevel(Level.OFF);
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(Level.OFF);
        }
    }

    private static void writeError(CLIErrorPayloadDTO payload) {
        try {
            System.err.println(CLIJsonAdapter.toJson(payload, JSON_MAPPER));
        } catch (Exception serializationError) {
            System.err.println(
                    "{\"errorCode\":\"INTERNAL_ERROR\",\"status\":25,\"title\":\"Internal error\",\"message\":\"Failed to serialize CLI error payload.\"}"
            );
        }
    }
}
