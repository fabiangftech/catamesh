package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.infrastructure.config.AppConfig;

@SuppressWarnings("java:S106")
public class DataProductCLICommand {
    private static final AppConfig appConfig = new AppConfig();

    public static void main(String[] args) {
        String verb = args[0];  // plan - apply - get - diff
        String yaml;
        String dataProductName;
        String json;
        switch (verb) {
            case "diff":
                yaml = args[1];
                DiffResult diffResult = appConfig.dataProductFacade().diff(yaml);
                json = appConfig.jsonMapper().writeValueAsString(diffResult);
                System.out.println(json);
                break;
            case "plan":
                yaml = args[1];
                PlanResult   planResult = appConfig.dataProductFacade().plan(yaml);
                json = appConfig.jsonMapper().writeValueAsString(planResult);
                System.out.println(json);
                break;
            case "apply":
                yaml = args[1];
                ApplyResult applyResult = appConfig.dataProductFacade().apply(yaml);
                json = appConfig.jsonMapper().writeValueAsString(applyResult);
                System.out.println(json);
                break;
            case "get":
                dataProductName = args[2];
                //DataProduct dataProduct = dataProductFacade.get(dataProductName);
                //System.out.println(CLIJsonAdapter.toJson(dataProduct, jsonMapper));
                break;
        }
    }
}
