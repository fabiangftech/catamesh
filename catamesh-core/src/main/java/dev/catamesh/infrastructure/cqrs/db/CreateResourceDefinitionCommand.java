package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.Resource;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.infrastructure.adapter.ResourceDefinitionAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class CreateResourceDefinitionCommand implements Command<Resource, Resource> {
    private static final Logger logger = Logger.getLogger(CreateResourceDefinitionCommand.class.getName());
    private static final String SQL_INSERT = """
            INSERT INTO resource_definition
            (id, schema_version, resource_id, version, active, config)
            VALUES (?, ?, ?, ?, ?, ?::json)
            """;
    private static final String SQL_INSERT_H2 = """
            INSERT INTO resource_definition
            (id, schema_version, resource_id, version, active, config)
            VALUES (?, ?, ?, ?, ?, ? FORMAT JSON)
            """;

    private final DataSource dataSource;
    private final ObjectMapper jsonMapper;

    public CreateResourceDefinitionCommand(DataSource dataSource, ObjectMapper jsonMapper) {
        this.dataSource = dataSource;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Resource execute(Resource resource) {
        ResourceDefinition definition = resource.getDefinition();

        Key definitionId = Key.newId();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(resolveSqlInsert(connection))) {
            preparedStatement.setString(1, definitionId.value());
            preparedStatement.setString(2, definition.getSchemaVersion().getValue());
            preparedStatement.setString(3, resource.getId());
            preparedStatement.setString(4, definition.getVersion());
            preparedStatement.setBoolean(5, true);
            String configJson = ResourceDefinitionAdapter.toConfigJson(definition, jsonMapper);

            preparedStatement.setString(6, configJson);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String message = String.format("Error saving resource definition for resource(name=%s)", resource.getName());
            logger.log(Level.SEVERE, message, e);
            throw new DependencyException(message);
        } catch (JacksonException e) {
            String message = String.format("Error mapping resource definition to JSON for resource(name=%s)", resource.getName());
            logger.log(Level.SEVERE, message, e);
            throw new MappingException(message);
        }
        return resource;
    }

    private String resolveSqlInsert(Connection connection) throws SQLException {
        String databaseName = connection.getMetaData().getDatabaseProductName();
        if (Objects.nonNull(databaseName) && databaseName.equalsIgnoreCase("H2")) {
            return SQL_INSERT_H2;
        }
        return SQL_INSERT;
    }
}
