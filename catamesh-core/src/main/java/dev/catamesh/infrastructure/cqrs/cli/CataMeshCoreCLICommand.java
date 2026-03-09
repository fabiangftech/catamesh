package dev.catamesh.infrastructure.cqrs.cli;

public class CataMeshCoreCLICommand {

    public static void main(String[] args) {
        String verb = args[0];
        switch (verb) {
            case "new":
                NewTemplateCLICommand.main(args);
                break;
            case "plan":
                System.out.println("Not implemented plan yet!");
                break;
            case "apply":
                System.out.println("Not implemented apply yet!");
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
}
