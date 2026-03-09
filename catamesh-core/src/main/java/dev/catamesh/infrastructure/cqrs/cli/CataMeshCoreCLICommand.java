package dev.catamesh.infrastructure.cqrs.cli;

public class CataMeshCoreCLICommand {

    public static void main(String[] command) {
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
                System.out.println("Not implemented get yet!");
                break;
            case "destroy":
                System.out.println("Not implemented destroy yet!");
                break;
            default:
                break;
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
}
