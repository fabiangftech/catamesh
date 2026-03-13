package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.infrastructure.adapter.CLIJsonAdapter;
import dev.catamesh.infrastructure.config.v2.AppConfig;

@SuppressWarnings("java:S106")
public class DataProductCLICommand {
    private static final AppConfig appConfig = new AppConfig();

    public static void main(String[] args) {
        String verb = args[0];  // plan - apply - get - diff
        String yaml;
        String dataProductName;
        switch (verb) {
            case "diff":
                yaml = args[1];
                DiffResult diff = appConfig.dataProductFacade().diff(yaml);
                System.out.println(CLIJsonAdapter.toJson(diff, appConfig.jsonMapper()));
                break;
            case "plan":
                yaml = args[1];
                PlanResult plan = appConfig.dataProductFacade().plan(yaml);
                System.out.println(CLIJsonAdapter.toJson(plan, appConfig.jsonMapper()));
                break;
            case "apply":
                yaml = args[1];
                //ApplyResult applyResult = dataProductFacade.apply(yaml);
                //System.out.println(CLIJsonAdapter.toJson(applyResult, jsonMapper));
                break;
            case "get":
                dataProductName = args[2];
                //DataProduct dataProduct = dataProductFacade.get(dataProductName);
                //System.out.println(CLIJsonAdapter.toJson(dataProduct, jsonMapper));
                break;
        }
    }
}
