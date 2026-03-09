package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Query;
import cl.guaman.weave.core.exception.DependencyException;
import cl.guaman.weave.core.exception.MappingException;
import cl.guaman.weave.core.exception.NotFoundException;
import cl.guaman.weave.core.model.Key;
import cl.guaman.weave.core.model.ResourceDefinition;
import cl.guaman.weave.infrastructure.adapter.ResourceDefinitionAdapter;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
@Named("getResourceDefinitionQuery")
public class GetResourceDefinitionQuery implements Query<Key, ResourceDefinition> {

    private static final Logger logger = LoggerFactory.getLogger(GetResourceDefinitionQuery.class);

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
                                      @Named("jsonMapper") ObjectMapper jsonMapper) {
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

            logger.error(message, e);

            throw new DependencyException(message);

        } catch (JacksonException e) {
            String message = String.format(
                    "Error mapping active resource definition (resourceId=%s)",
                    resourceId.value()
            );

            logger.error(message, e);

            throw new MappingException(message);
        }
    }
}