package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.infrastructure.dto.GetResourceDefinitionDTO;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OptionalResourceDefinitionQuery implements Query<GetResourceDefinitionDTO, Optional<String>> {
    private static final Logger logger = Logger.getLogger(OptionalResourceDefinitionQuery.class.getName());
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

            logger.log(Level.SEVERE, message, e);

            throw new DependencyException(message);
        }
    }
}
