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

public class CQRSConfig {

    private final Query<String, List<Resource>> allResourcesQuery;
    private final Command<DataProduct, DataProduct> createDataProductCommand;
    private final Command<Resource, Void> createResourceCommand;
    private final Command<Resource, Resource> createResourceDefinitionCommand;
    private final Query<Key, ResourceDefinition> getResourceDefinitionQuery;
    private final Command<Void, Void> initTablesDBCommand;
    private final Query<String, Optional<DataProduct>> optionalDataProductQuery;
    private final Query<GetResourceDefinitionDTO, Optional<String>> optionalResourceDefinitionQuery;
    private final Query<GetResourceDefinitionDTO, Optional<ResourceDefinition>> optionalResourceDefinitionVersionQuery;
    private final Command<DataProduct, DataProduct> updateDataProductCommand;
    private final Query<String, String> getFileFromResourceQuery;

    public CQRSConfig(DataSource dataSource, ObjectMapper jsonMapper) {
        this.getFileFromResourceQuery = new GetFileFromResourceQuery();
        this.allResourcesQuery = new AllResourcesQuery(dataSource);
        this.createDataProductCommand = new CreateDataProductCommand(dataSource);
        this.createResourceCommand = new CreateResourceCommand(dataSource);
        this.createResourceDefinitionCommand = new CreateResourceDefinitionCommand(dataSource, jsonMapper);
        this.getResourceDefinitionQuery = new GetResourceDefinitionQuery(dataSource, jsonMapper);
        this.initTablesDBCommand = new InitTablesDBCommand(dataSource, this.getFileFromResourceQuery);
        this.optionalDataProductQuery = new OptionalDataProductQuery(dataSource);
        this.optionalResourceDefinitionQuery = new OptionalResourceDefinitionQuery(dataSource);
        this.optionalResourceDefinitionVersionQuery = new OptionalResourceDefinitionVersionQuery(dataSource, jsonMapper);
        this.updateDataProductCommand = new UpdateDataProductCommand(dataSource);
    }

    public Query<String, String> getFileFromResourceQuery() {
        return getFileFromResourceQuery;
    }

    public Query<String, List<Resource>> getAllResourcesQuery() {
        return allResourcesQuery;
    }

    public Command<DataProduct, DataProduct> getCreateDataProductCommand() {
        return createDataProductCommand;
    }

    public Command<Resource, Void> getCreateResourceCommand() {
        return createResourceCommand;
    }

    public Command<Resource, Resource> getCreateResourceDefinitionCommand() {
        return createResourceDefinitionCommand;
    }

    public Query<Key, ResourceDefinition> getResourceDefinitionQuery() {
        return getResourceDefinitionQuery;
    }

    public Command<Void, Void> getInitTablesDBCommand() {
        return initTablesDBCommand;
    }

    public Query<String, Optional<DataProduct>> getOptionalDataProductQuery() {
        return optionalDataProductQuery;
    }

    public Query<GetResourceDefinitionDTO, Optional<String>> getOptionalResourceDefinitionQuery() {
        return optionalResourceDefinitionQuery;
    }

    public Query<GetResourceDefinitionDTO, Optional<ResourceDefinition>> getOptionalResourceDefinitionVersionQuery() {
        return optionalResourceDefinitionVersionQuery;
    }

    public Command<DataProduct, DataProduct> getUpdateDataProductCommand() {
        return updateDataProductCommand;
    }
}
