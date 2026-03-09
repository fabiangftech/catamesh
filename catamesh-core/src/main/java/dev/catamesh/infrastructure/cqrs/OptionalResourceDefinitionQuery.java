package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Query;
import cl.guaman.weave.core.exception.DependencyException;
import cl.guaman.weave.infrastructure.dto.GetResourceDefinitionDTO;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Singleton
public class OptionalResourceDefinitionQuery implements Query<GetResourceDefinitionDTO, Optional<String>> {
    private static final Logger logger = LoggerFactory.getLogger(OptionalResourceDefinitionQuery.class);
    private static final String SQL_QUERY_BY_RESOURCE_AND_SCHEMA_AND_VERSION = """
            SELECT id
            FROM resource_definition
            WHERE resource_id = ?
              AND version = ?
            LIMIT 1
            """;

    private final DataSource dataSource;

    public OptionalResourceDefinitionQuery(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<String> execute(GetResourceDefinitionDTO dto) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_QUERY_BY_RESOURCE_AND_SCHEMA_AND_VERSION)) {
            ps.setString(1, dto.resourceId());
            ps.setString(2, dto.version());

            try (ResultSet resultSet = ps.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(resultSet.getString("id"));
            }
        } catch (SQLException e) {
            String message = String.format(
                    "Database error querying resource definition (resourceId=%s, version=%s)",
                    dto.resourceId(),
                    dto.version()
            );

            logger.error(message, e);

            throw new DependencyException(message);
        }
    }
}
