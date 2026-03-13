package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.DiffResult;
import dev.catamesh.core.model.PlanResult;
import dev.catamesh.infrastructure.adapter.DiffToStringAdapter;
import dev.catamesh.infrastructure.adapter.PlanToStringAdapter;
import dev.catamesh.infrastructure.config.AppConfig;

import static dev.catamesh.infrastructure.cqrs.cli.CataMeshCoreCLICommand.*;

@SuppressWarnings("java:S106")
public class DataProductCLICommand {
    private static final AppConfig appConfig = new AppConfig();

    public static void main(String[] args) {
        String verb = args[0];
        String yaml;
        String dataProductName;
        String json;
        String result;
        switch (verb) {
            case DIFF:
                yaml = args[1];
                DiffResult diffResult = appConfig.dataProductFacade().diff(yaml);
                result = DiffToStringAdapter.toString(diffResult);
                System.out.println(result);
                break;
            case PLAN:
                yaml = args[1];
                PlanResult planResult = appConfig.dataProductFacade().plan(yaml);
                result = PlanToStringAdapter.toString(planResult);
                System.out.println(result);
                break;
            case APPLY:
                yaml = args[1];
                ApplyResult applyResult = appConfig.dataProductFacade().apply(yaml);
                json = appConfig.jsonMapper().writeValueAsString(applyResult);
                System.out.println(json);
                break;
            case GET:
                dataProductName = args[2];
                //DataProduct dataProduct = dataProductFacade.get(dataProductName);
                //System.out.println(CLIJsonAdapter.toJson(dataProduct, jsonMapper));
                break;
        }
    }
}
