package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.model.Plan;
import dev.catamesh.infrastructure.config.ApplicationConfig;

public class DataProductCLICommand {
    private static final ApplicationConfig applicationConfig = new ApplicationConfig();
    private static final DataProductFacade dataProductFacade = applicationConfig.dataProductFacade();

    public static void main(String[] args) {
        String verb = args[0];  // plan - apply - get - diff
        String yaml = args[1];
        switch (verb) {
            case "plan":
                Plan plan = dataProductFacade.plan(yaml);
                System.out.println(plan.toString());
                break;
            case "apply":
                break;
            case "get":
                break;
            case "diff":
                break;
        }
    }
}
