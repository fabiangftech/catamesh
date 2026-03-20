package dev.catamesh.infrastructure.config;

import dev.catamesh.application.facade.PlanEngineFacade;
import dev.catamesh.application.factory.PlanDataProductChainFactory;
import dev.catamesh.application.handler.*;
import dev.catamesh.application.strategy.PlanMetadataStrategy;
import dev.catamesh.application.strategy.PlanResourcesStrategy;
import dev.catamesh.application.strategy.PlanSpecStrategy;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.PlanDataProductContext;
import dev.catamesh.core.model.*;
import dev.catamesh.core.strategy.PlanStrategy;
import dev.catamesh.infrastructure.cqrs.db.AllResourcesQuery;
import dev.catamesh.infrastructure.cqrs.db.GetResourceDefinitionQuery;
import dev.catamesh.infrastructure.cqrs.db.OptionalDataProductQuery;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class PlanConfig {
    private final DataSource dataSource;

    public PlanConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Factory<Void, Handler<PlanDataProductContext>> planDataProductChainFactory() {
        Query<String, Optional<DataProduct>> optionalDataProductQuery = new OptionalDataProductQuery(dataSource);
        Query<String, List<Resource>> allResourcesQuery = new AllResourcesQuery(dataSource);
        Query<Key, ResourceDefinition> getResourceDefinitionQuery = new GetResourceDefinitionQuery(dataSource, JSONConfig.jsonMapper());


        PlanStrategy planMetadataStrategy = new PlanMetadataStrategy();
        PlanStrategy planResourcesStrategy = new PlanResourcesStrategy();
        PlanStrategy planSpecStrategy = new PlanSpecStrategy(planResourcesStrategy);
        PlanEngineFacade planEngineFacade = new PlanEngineFacade(planMetadataStrategy, planSpecStrategy);

        Handler<PlanDataProductContext> getCurrentDataProductHandler = new GetCurrentDataProductHandler<>(optionalDataProductQuery, allResourcesQuery, getResourceDefinitionQuery);
        Handler<PlanDataProductContext> buildDiffDataProductHandler = new BuildDiffDataProductHandler<>();
        Handler<PlanDataProductContext> buildPlanDataProductHandler = new BuildPlanDataProductHandler<>(planEngineFacade);

        return PlanDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .add(getCurrentDataProductHandler)
                .add(buildDiffDataProductHandler)
                .add(buildPlanDataProductHandler)
                .build();
    }
}
