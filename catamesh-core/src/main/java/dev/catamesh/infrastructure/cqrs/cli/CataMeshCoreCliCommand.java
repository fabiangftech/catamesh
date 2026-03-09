package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.application.facade.DefaultTemplateFacade;
import dev.catamesh.core.facade.TemplateFacade;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;

import java.io.PrintStream;

public class CataMeshCoreCliCommand {

    private static final TemplateFacade templateFacade = new DefaultTemplateFacade(new GetFileFromResourceQuery());
    private static final int SUCCESS = 0;
    private static final int FAILURE = 1;

    public static void main(String[] args) {
        System.exit(execute(args, System.out, System.err));
    }

    static int execute(String[] args, PrintStream out, PrintStream err) {
        if (args.length == 0) {
            return printUsage(err);
        }

        String command = args[0];
        switch (command) {
            case "init":
                return executeInit(args, out, err);
            case "plan":
            case "apply":
            case "diff":
                out.println("Hello CataMesh!");
                return SUCCESS;
            default:
                err.printf("Invalid command: %s%n", command);
                return printUsage(err);
        }
    }

    private static int executeInit(String[] args, PrintStream out, PrintStream err) {
        if (args.length < 2) {
            err.println("Missing init kind.");
            return printUsage(err);
        }

        String kind = args[1];
        switch (kind) {
            case "data-product":
                if (args.length < 3) {
                    err.println("Missing data-product name.");
                    err.println("Usage: cata init data-product <name>");
                    return FAILURE;
                }

                String dataProductName = args[2];
                String yaml = templateFacade.initDataProduct(dataProductName);
                out.println(yaml);
                return SUCCESS;
            case "deploy":
            case "env":
            default:
                err.printf("Invalid init option: %s%n", kind);
                err.println("Usage: cata init data-product <name>");
                return FAILURE;
        }
    }

    private static int printUsage(PrintStream err) {
        err.println("Usage: cata <command> [args]");
        err.println("Commands: init, plan, apply, diff");
        err.println("Example: cata init data-product my-first-data-product");
        return FAILURE;
    }
}
