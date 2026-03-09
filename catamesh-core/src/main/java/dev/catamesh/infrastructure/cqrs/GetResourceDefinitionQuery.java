package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.exception.NotFoundException;
import dev.catamesh.core.model.Key;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.infrastructure.adapter.ResourceDefinitionAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetResourceDefinitionQuery implements Query<Key, ResourceDefinition> {

    private static final Logger logger = Logger.getLogger(GetResourceDefinitionQuery.class.getName());

    private static final String SQL_QUERY_ACTIVE_BY_RESOURCE_ID = """
            SELECT schema_version, version, config
            FROM resource_definition
            WHERE resource_id = ?
              AND active = true
            LIMIT 1
            """;

    private final DataSource dataSource;
    private final ObjectMapper jsonMapper;

    public GetResourceDefinitionQuery(DataSource dataSource,
                                      ObjectMapper jsonMapper) {
        this.dataSource = dataSource;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public ResourceDefinition execute(Key resourceId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_QUERY_ACTIVE_BY_RESOURCE_ID)) {

            ps.setString(1, resourceId.value());

            try (ResultSet resultSet = ps.executeQuery()) {
                if (!resultSet.next()) {
                    throw new NotFoundException(
                            String.format(
                                    "Active resource definition does not exist for resourceId=%s",
                                    resourceId.value()
                            )
                    );
                }

                return ResourceDefinitionAdapter.toResourceDefinition(resultSet, jsonMapper);
            }

        } catch (SQLException e) {
            String message = String.format(
                    "Database error querying active resource definition (resourceId=%s)",
                    resourceId.value()
            );

            logger.log(Level.SEVERE, message, e);

            throw new DependencyException(message);

        } catch (JacksonException e) {
            String message = String.format(
                    "Error mapping active resource definition (resourceId=%s)",
                    resourceId.value()
            );

            logger.log(Level.SEVERE, message, e);

            throw new MappingException(message);
        }
    }
}