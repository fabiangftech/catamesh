package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.model.ResourceDefinition;
import dev.catamesh.infrastructure.adapter.ResourceDefinitionAdapter;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OptionalResourceDefinitionVersionQuery implements Query<GetResourceDefinitionDTO, Optional<ResourceDefinition>> {
    private static final Logger logger = Logger.getLogger(OptionalResourceDefinitionVersionQuery.class.getName());
    private static final String SQL_QUERY_BY_RESOURCE_AND_VERSION = """
            SELECT schema_version, version, config
            FROM resource_definition
            WHERE resource_id = ?
              AND version = ?
            LIMIT 1
            """;

    private final DataSource dataSource;
    private final ObjectMapper jsonMapper;

    public OptionalResourceDefinitionVersionQuery(DataSource dataSource, ObjectMapper jsonMapper) {
        this.dataSource = dataSource;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public Optional<ResourceDefinition> execute(GetResourceDefinitionDTO dto) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_QUERY_BY_RESOURCE_AND_VERSION)) {
            ps.setString(1, dto.resourceId());
            ps.setString(2, dto.version());

            try (ResultSet resultSet = ps.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(ResourceDefinitionAdapter.toResourceDefinition(resultSet, jsonMapper));
            }
        } catch (SQLException e) {
            String message = String.format(
                    "Database error querying resource definition content (resourceId=%s, version=%s)",
                    dto.resourceId(),
                    dto.version()
            );
            logger.log(Level.SEVERE, message, e);
            throw new DependencyException(message);
        } catch (JacksonException e) {
            String message = String.format(
                    "Error mapping resource definition content (resourceId=%s, version=%s)",
                    dto.resourceId(),
                    dto.version()
            );
            logger.log(Level.SEVERE, message, e);
            throw new MappingException(message);
        }
    }
}
