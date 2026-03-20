package dev.catamesh.infrastructure.config;

import dev.catamesh.application.facade.PlanEngineFacade;
import dev.catamesh.application.factory.ApplyDataProductChainFactory;
import dev.catamesh.application.handler.*;
import dev.catamesh.application.strategy.ValidateImmutabilityPolicyRuleStrategy;
import dev.catamesh.application.strategy.PlanMetadataStrategy;
import dev.catamesh.application.strategy.PlanResourcesStrategy;
import dev.catamesh.application.strategy.PlanSpecStrategy;
import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.ApplyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.PlanDataProductContext;
import dev.catamesh.core.model.*;
import dev.catamesh.core.strategy.PlanStrategy;
import dev.catamesh.core.strategy.PolicyRuleStrategy;
import dev.catamesh.infrastructure.cqrs.db.*;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class ApplyConfig {

    public Factory<Void, Handler<ApplyDataProductContext>> applynDataProductChainFactory() {
        Query<String, Optional<DataProduct>> optionalDataProductQuery = new OptionalDataProductQuery(DataSourceConfig.get());
        Query<String, List<Resource>> allResourcesQuery = new AllResourcesQuery(DataSourceConfig.get());
        Query<Key, ResourceDefinition> getResourceDefinitionQuery = new GetResourceDefinitionQuery(DataSourceConfig.get(), JSONConfig.jsonMapper());

        PlanStrategy planMetadataStrategy = new PlanMetadataStrategy();
        PlanStrategy planResourcesStrategy = new PlanResourcesStrategy();
        PlanStrategy planSpecStrategy = new PlanSpecStrategy(planResourcesStrategy);
        PlanEngineFacade planEngineFacade = new PlanEngineFacade(planMetadataStrategy, planSpecStrategy);

        Command<DataProduct, DataProduct> createDataProductCommand = new CreateDataProductCommand(DataSourceConfig.get());
        Command<Resource, Void> createResourceCommand = new CreateResourceCommand(DataSourceConfig.get());
        Command<Resource, Resource> createResourceDefinitionCommand = new CreateResourceDefinitionCommand(DataSourceConfig.get(), JSONConfig.jsonMapper());
        Command<DataProduct, DataProduct> updateDataProductCommand = new UpdateDataProductCommand(DataSourceConfig.get());
        Command<Resource, Resource> updateResourceCommand = new UpdateResourceCommand(DataSourceConfig.get());

        Handler<ApplyDataProductContext> getCurrentDataProductHandler = new GetCurrentDataProductHandler<>(optionalDataProductQuery, allResourcesQuery, getResourceDefinitionQuery);
        Handler<ApplyDataProductContext> buildDiffDataProductHandler = new BuildDiffDataProductHandler<>();
        Handler<ApplyDataProductContext> buildPlanDataProductHandler = new BuildPlanDataProductHandler<>(planEngineFacade);
        Handler<ApplyDataProductContext> initializeApplyDataProductHandler = new InitializeApplyDataProductHandler<>();
        Handler<ApplyDataProductContext> createDataProductHandler = new CreateDataProductHandler<>(createDataProductCommand);
        Handler<ApplyDataProductContext> createResourcesDataProductHandler = new CreateResourcesHandler<>(createResourceCommand);
        Handler<ApplyDataProductContext> createResourcesDefinitionsHandler = new CreateResourcesDefinitionsHandler<>(createResourceDefinitionCommand);
        Handler<ApplyDataProductContext> updateDataProductHandler = new UpdateDataProductHandler<>(updateDataProductCommand);
        Handler<ApplyDataProductContext> updateResourceHandler = new UpdateResourceHandler<>(updateResourceCommand);
        Handler<ApplyDataProductContext> buildApplyDataProductHandler = new BuildApplyDataProductHandler<>();

        return ApplyDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.validateImmutabilityHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .add(getCurrentDataProductHandler)
                .add(buildDiffDataProductHandler)
                .add(buildPlanDataProductHandler)
                .add(initializeApplyDataProductHandler)
                .add(createDataProductHandler)
                .add(createResourcesDataProductHandler)
                .add(createResourcesDefinitionsHandler)
                .add(updateDataProductHandler)
                .add(updateResourceHandler)
                .add(buildApplyDataProductHandler)
                .build();
    }
}
