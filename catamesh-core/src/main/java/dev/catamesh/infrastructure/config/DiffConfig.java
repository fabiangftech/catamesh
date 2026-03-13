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
        YAMLConfig yamlConfig = new YAMLConfig();
        JSONConfig jsonConfig = new JSONConfig();

        Query<String, Optional<DataProduct>> optionalDataProductQuery = new OptionalDataProductQuery(dataSource);
        Query<String, List<Resource>> allResourcesQuery = new AllResourcesQuery(dataSource);
        Query<Key, ResourceDefinition> getResourceDefinitionQuery = new GetResourceDefinitionQuery(dataSource, jsonConfig.jsonMapper());

        Handler<DiffDataProductContext> yamlToDataProductHandler = new YAMLToDataProductHandler<>(yamlConfig.yamlMapper());
        Handler<DiffDataProductContext> validateDataProductSchemaHandler = new ValidateDataProductSchemaHandler<>(jsonConfig.dataProductSchema(), jsonConfig.jsonMapper());
        Handler<DiffDataProductContext> validateResourceSchemaHandler = new ValidateResourceSchemaHandler<>(jsonConfig.resourceSchema(), jsonConfig.jsonMapper());
        Handler<DiffDataProductContext> validateBucketDefinitionSchemaHandler = new ValidateBucketDefinitionSchemaHandler<>(jsonConfig.bucketSchema(), jsonConfig.jsonMapper());
        Handler<DiffDataProductContext> getCurrentDataProductHandler = new GetCurrentDataProductHandler<>(optionalDataProductQuery, allResourcesQuery, getResourceDefinitionQuery);
        Handler<DiffDataProductContext> buildDiffDataProductHandler = new BuildDiffDataProductHandler<>();

        return new DiffDataProductChainFactory(
                yamlToDataProductHandler,
                validateDataProductSchemaHandler,
                validateResourceSchemaHandler,
                validateBucketDefinitionSchemaHandler,
                getCurrentDataProductHandler,
                buildDiffDataProductHandler
        );
    }
}
