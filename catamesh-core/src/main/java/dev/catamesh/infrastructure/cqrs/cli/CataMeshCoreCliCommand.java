package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.application.facade.DefaultTemplateFacade;
import dev.catamesh.core.facade.TemplateFacade;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;

public class CataMeshCoreCliCommand {
    private static final TemplateFacade templateFacade = new DefaultTemplateFacade(new GetFileFromResourceQuery());
    public static void main(String[] args) {
        String dataProductName = args[0];
        String yaml = templateFacade.initDataProduct(dataProductName);
        System.out.println(yaml);
    }
}
