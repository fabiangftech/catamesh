package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Command;
import cl.guaman.weave.core.exception.DependencyException;
import cl.guaman.weave.core.exception.MappingException;
import cl.guaman.weave.core.model.Key;
import cl.guaman.weave.core.model.Resource;
import cl.guaman.weave.core.model.ResourceDefinition;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Singleton
@Named("createResourceDefinitionCommand")
public class CreateResourceDefinitionCommand implements Command<Resource, Resource> {
    private static final Logger logger = LoggerFactory.getLogger(CreateResourceDefinitionCommand.class);
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

    public CreateResourceDefinitionCommand(DataSource dataSource, @Named("jsonMapper") ObjectMapper jsonMapper) {
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
            String configJson = jsonMapper.writeValueAsString(definition.getConfig());

            preparedStatement.setString(6, configJson);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String message = String.format("Error saving resource definition for resource(name=%s)", resource.getName());
            logger.error(message, e);
            throw new DependencyException(message);
        } catch (JacksonException e) {
            String message = String.format("Error mapping resource definition to JSON for resource(name=%s)", resource.getName());
            logger.error(message, e);
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
