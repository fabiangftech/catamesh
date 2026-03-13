package dev.catamesh.infrastructure.cqrs.cli;

import java.util.stream.IntStream;

public class CataMeshCoreCLICommand {
    public static final String DIFF = "diff";
    public static final String PLAN = "plan";
    public static final String APPLY = "apply";
    public static final String GET = "get";

    public static void main(String[] command) {
        String[] aux =
                IntStream.range(0, command.length).filter(i -> i != 1)
                        .mapToObj(i -> command[i])
                        .toArray(String[]::new);
        try {
            switch (command[0]) {
                case DIFF, PLAN, APPLY, GET -> {
                    if (command[1].equals("data-product")) {
                        DataProductCLICommand.main(aux);
                    }
                }
                default -> {
                }
            }
        } catch (Exception e) {
            //todo check exception
        }
    }
}
