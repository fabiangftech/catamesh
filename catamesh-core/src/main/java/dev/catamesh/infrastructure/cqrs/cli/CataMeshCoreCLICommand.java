package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.infrastructure.adapter.CLIAdapter;

public class CataMeshCoreCLICommand {
    public static final String INIT = "init";
    public static final String DIFF = "diff";
    public static final String PLAN = "plan";
    public static final String APPLY = "apply";
    public static final String GET = "get";
    public static final String PULL = "pull";

    public static void main(String[] command) {
        try {
            switch (command[0]) {
                case DIFF, PLAN, APPLY, GET, PULL -> {
                    String[] auxCommand = CLIAdapter.removeKind(command);
                    if (command[1].equals("data-product")) {
                        DataProductCLICommand.main(auxCommand);
                    }
                }
                case INIT -> {
                    if (command[1].equals("data-product")) {
                        NewTemplateCLICommand.main(command);
                    }
                }
                default -> {
                    throw new IllegalArgumentException();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
