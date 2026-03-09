package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.infrastructure.config.ApplicationConfig;

public class CataMeshCoreCliCommand {

    private static final ApplicationConfig applicationConfig = new ApplicationConfig();

    public static void main(String[] args) {
        String command = args[0];
        switch (command) {
            case "init":
                String kind = args[1];
                switch (kind) {
                    case "data-product":
                        String dataProductName = args[2];
                        String yaml = applicationConfig.templateFacade().initDataProduct(dataProductName);
                        System.out.println(yaml);
                    case "deploy":
                    case "env":
                    default:
                        System.out.println("option no valid");
                }
                break;
            case "plan":
            case "apply":
            case "diff":
            default:
                System.out.println("Hello CataMesh!");
                break;
        }

    }
}
