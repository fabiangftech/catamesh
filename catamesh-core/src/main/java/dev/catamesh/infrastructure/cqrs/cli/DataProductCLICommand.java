package dev.catamesh.infrastructure.cqrs.cli;

import dev.catamesh.core.model.*;
import dev.catamesh.infrastructure.adapter.*;
import dev.catamesh.infrastructure.config.AppConfig;

import static dev.catamesh.infrastructure.cqrs.cli.CataMeshCoreCLICommand.*;

@SuppressWarnings("java:S106")
public class DataProductCLICommand {
    private static final AppConfig appConfig = new AppConfig();

    public static void main(String[] args) {
        String verb = args[0];
        String yaml;
        DataProduct dataProduct;
        String dataProductName;
        String result;
        switch (verb) {
            case VALIDATE:
                yaml = args[1];
                ValidateResult validateResult = appConfig.dataProductFacade().validate(yaml);
                //todo validate-result to string
                break;
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
                result = ApplyToStringAdapter.toString(applyResult);
                System.out.println(result);
                break;
            case GET, PULL:
                dataProductName = args[1];
                dataProduct = appConfig.dataProductFacade().get(dataProductName);
                result = DataProductAdapter.toYaml(dataProduct);
                System.out.println(result);
                break;
        }
    }
}
