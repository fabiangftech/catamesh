package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.core.model.ModelType;

public class CataMeshCoreCLICommand {

    public static void main(String[] command) {
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
        } catch (Exception e) {
            System.err.println(e.getCause().getMessage());
            System.exit(1);
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
}
