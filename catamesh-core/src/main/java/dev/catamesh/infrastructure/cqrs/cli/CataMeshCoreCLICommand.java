package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.core.model.ModelType;
import dev.catamesh.infrastructure.adapter.CLIErrorAdapter;
import dev.catamesh.infrastructure.dto.CLIErrorPayloadDTO;
import tools.jackson.databind.ObjectMapper;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@SuppressWarnings("java:S106")
public class CataMeshCoreCLICommand {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    public static void main(String[] command) {
        int status = execute(command);
        if (status != 0) {
            System.exit(status);
        }
    }

    public static int execute(String[] command) {
        configureCliLogging();
        try {
            String verb = command[0];
            switch (verb) {
                case "new":
                    NewTemplateCLICommand.main(command);
                    break;
                case "plan":
                    plan(command);
                    break;
                case "apply":
                    apply(command);
                    break;
                case "get":
                    get(command);
                    break;
                case "diff":
                    diff(command);
                    break;
                case "destroy":
                    System.out.println("Not implemented destroy yet!");
                    break;
                default:
                    break;
            }
            return 0;
        } catch (Throwable error) {
            CLIErrorPayloadDTO payload = CLIErrorAdapter.map(error);
            writeError(payload);
            return payload.getStatus();
        }
    }

    private static void plan(String[] command) {
        //todo check if schema is data-product/v1
        DataProductCLICommand.main(command);
    }

    private static void apply(String[] command) {
        //todo check if schema is data-product/v1
        DataProductCLICommand.main(command);
    }

    private static void get(String[] command) {
        String type = command[1]; // data-product/deploy/env
        if (ModelType.DATA_PRODUCT.getValue().equals(type)) {
            DataProductCLICommand.main(command);
        }
    }

    private static void diff(String[] command){
        //todo check if schema is data-product/v1
        DataProductCLICommand.main(command);
    }

    private static void configureCliLogging() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        if (rootLogger == null) {
            return;
        }

        rootLogger.setLevel(Level.WARNING);
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(Level.WARNING);
        }
    }

    private static void writeError(CLIErrorPayloadDTO payload) {
        try {
            System.err.println(JSON_MAPPER.writeValueAsString(payload));
        } catch (Exception serializationError) {
            System.err.println(
                    "{\"errorCode\":\"INTERNAL_ERROR\",\"status\":25,\"title\":\"Internal error\",\"message\":\"Failed to serialize CLI error payload.\"}"
            );
        }
    }
}
