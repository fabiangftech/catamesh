package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.core.facade.DataProductFacade;
import dev.catamesh.core.model.ApplyResult;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Plan;
import dev.catamesh.core.model.v2.DiffResult;
import dev.catamesh.infrastructure.adapter.CLIJsonAdapter;
import dev.catamesh.infrastructure.config.ApplicationConfig;
import tools.jackson.databind.ObjectMapper;
@SuppressWarnings("java:S106")
public class DataProductCLICommand {
    private static final ApplicationConfig applicationConfig = new ApplicationConfig();
    private static final DataProductFacade dataProductFacade = applicationConfig.dataProductFacade();
    private static final ObjectMapper jsonMapper = applicationConfig.jsonMapper();


    public static void main(String[] args) {
        String verb = args[0];  // plan - apply - get - diff
        String yaml;
        String dataProductName;
        switch (verb) {
            case "plan":
                yaml = args[1];
                Plan plan = dataProductFacade.plan(yaml);
                System.out.println(CLIJsonAdapter.toJson(plan, jsonMapper));
                break;
            case "apply":
                yaml = args[1];
                ApplyResult applyResult = dataProductFacade.apply(yaml);
                System.out.println(CLIJsonAdapter.toJson(applyResult, jsonMapper));
                break;
            case "get":
                dataProductName = args[2];
                DataProduct dataProduct = dataProductFacade.get(dataProductName);
                System.out.println(CLIJsonAdapter.toJson(dataProduct, jsonMapper));
                break;
            case "diff":
                yaml = args[1];
                DiffResult diff = dataProductFacade.diff(yaml);
                System.out.println(CLIJsonAdapter.toJson(diff, jsonMapper));
                break;
        }
    }
}
