package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.infrastructure.config.AppConfig;
@SuppressWarnings("java:S106")
public class NewTemplateCLICommand {
    private static final AppConfig appConfig = new AppConfig();

    public static void main(String[] args) {
        String kind = args[1];
        switch (kind) {
            case "data-product":
                String name = args[2];
                String yaml = appConfig.templateFacade().initDataProduct(name);
                System.out.println(yaml);
                break;
            case "deploy":
                System.out.println("Not implemented deploy template yet!.");
                break;
            case "env":
                System.out.println("Not implemented env template yet!.");
                break;
            default:
                break;
        }
    }
}
