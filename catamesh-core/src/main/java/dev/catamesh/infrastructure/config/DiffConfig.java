package dev.catamesh.infrastructure.config;

import dev.catamesh.application.factory.DiffDataProductChainFactory;
import dev.catamesh.application.handler.*;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.factory.Factory;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.handler.DiffDataProductContext;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.infrastructure.cqrs.db.AllResourcesQuery;
import dev.catamesh.infrastructure.cqrs.db.GetResourceDefinitionQuery;
import dev.catamesh.infrastructure.cqrs.db.OptionalDataProductQuery;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public class DiffConfig {
    private final DataSource dataSource;

    public DiffConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Factory<Void, Handler<DiffDataProductContext>> diffDataProductChainFactory() {
        Query<String, Optional<DataProduct>> optionalDataProductQuery = new OptionalDataProductQuery(dataSource);
        Query<String, List<Resource>> allResourcesQuery = new AllResourcesQuery(dataSource);
        Query<Key, ResourceDefinition> getResourceDefinitionQuery = new GetResourceDefinitionQuery(dataSource, JSONConfig.jsonMapper());

        Handler<DiffDataProductContext> getCurrentDataProductHandler = new GetCurrentDataProductHandler<>(optionalDataProductQuery, allResourcesQuery, getResourceDefinitionQuery);
        Handler<DiffDataProductContext> buildDiffDataProductHandler = new BuildDiffDataProductHandler<>();

        return DiffDataProductChainFactory.builder()
                .add(HandlerConfig.yamlToDataProductHandler())
                .add(HandlerConfig.validateDataProductSchemaHandler())
                .add(HandlerConfig.validateResourceSchemaHandler())
                .add(HandlerConfig.validateBucketDefinitionSchemaHandler())
                .add(getCurrentDataProductHandler)
                .add(buildDiffDataProductHandler)
                .build();

    }
}
