package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.infrastructure.config.ApplicationConfig;

public class CataMeshCoreCliCommand {

    private static final ApplicationConfig applicationConfig = new ApplicationConfig();

    public static void main(String[] args) {
        String dataProductName = args[0];
        String yaml = applicationConfig.templateFacade().initDataProduct(dataProductName);
        System.out.println(yaml);
    }
}
