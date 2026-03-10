package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.core.facade.TemplateFacade;
import dev.catamesh.infrastructure.config.ApplicationConfig;

@SuppressWarnings("java:S106")
public class NewTemplateCLICommand {
    private static final ApplicationConfig applicationConfig = new ApplicationConfig();
    private static final TemplateFacade templateFacade = applicationConfig.templateFacade();
    public static void main(String[] args) {
        String kind = args[1]; // data-product - deploy - env
        switch (kind) {
            case "data-product":
                String name = args[2];
                String yaml = templateFacade.initDataProduct(name);
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
