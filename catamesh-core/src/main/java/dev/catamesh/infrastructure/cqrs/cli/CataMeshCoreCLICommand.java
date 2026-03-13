package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.infrastructure.adapter.CLIAdapter;

import java.util.stream.IntStream;

public class CataMeshCoreCLICommand {
    public static final String INIT = "init";
    public static final String DIFF = "diff";
    public static final String PLAN = "plan";
    public static final String APPLY = "apply";
    public static final String GET = "get";

    public static void main(String[] command) {
        try {
            switch (command[0]) {
                case DIFF, PLAN, APPLY, GET -> {
                    String[] aux = CLIAdapter.removeKind(command);
                    if (command[1].equals("data-product")) {
                        DataProductCLICommand.main(aux);
                    }
                }
                case INIT -> {
                    if (command[1].equals("data-product")) {
                        NewTemplateCLICommand.main(command);
                    }
                }
                default -> {
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
