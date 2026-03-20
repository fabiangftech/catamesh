package dev.catamesh.infrastructure.config;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.infrastructure.cqrs.db.*;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

public final class CQRSConfig {
    private CQRSConfig() {
        // do nothing
    }

    public static Query<GetResourceDefinitionDTO, Optional<ResourceDefinition>> optionalResourceDefinitionVersionQuery(DataSource dataSource) {
        return new OptionalResourceDefinitionVersionQuery(dataSource, JSONConfig.jsonMapper());
    }

    public static Query<String, Optional<DataProduct>> optionalDataProductQuery() {
        return new OptionalDataProductQuery(DataSourceConfig.get());
    }

    public static Query<String, List<Resource>> allResourcesQuery() {
        return new AllResourcesQuery(DataSourceConfig.get());
    }

    public static Query<Key, ResourceDefinition> getResourceDefinitionQuery() {
        return new GetResourceDefinitionQuery(DataSourceConfig.get(), JSONConfig.jsonMapper());
    }

    public static Command<DataProduct, DataProduct> createDataProductCommand() {
        return new CreateDataProductCommand(DataSourceConfig.get());
    }

    public static Command<Resource, Void> createResourceCommand() {
        return new CreateResourceCommand(DataSourceConfig.get());
    }

    public static Command<Resource, Resource> createResourceDefinitionCommand() {
        return new CreateResourceDefinitionCommand(DataSourceConfig.get(), JSONConfig.jsonMapper());
    }

    public static Command<DataProduct, DataProduct> updateDataProductCommand() {
        return new UpdateDataProductCommand(DataSourceConfig.get());
    }

    public static Command<Resource, Resource> updateResourceCommand() {
        return new UpdateResourceCommand(DataSourceConfig.get());
    }

    public static Query<String, String> getFileFromResourceQuery() {
        return new GetFileFromResourceQuery();
    }

    public static Command<Void, Void> initTablesDBCommand() {
        return new InitTablesDBCommand(DataSourceConfig.get(), CQRSConfig.getFileFromResourceQuery());
    }
}
