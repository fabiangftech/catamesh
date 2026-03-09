package dev.catamesh.infrastructure.cqrs;


import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.model.Key;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class CountResourceDefinitionsByResourceIdQuery implements Query<Key, Integer> {
    private static final Logger logger = Logger.getLogger(CountResourceDefinitionsByResourceIdQuery.class.getName());
    private static final String SQL_COUNT = """
            SELECT COUNT(*) AS total
            FROM resource_definition
            WHERE resource_id = ?
            """;

    private final DataSource dataSource;

    public CountResourceDefinitionsByResourceIdQuery(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Integer execute(Key resourceId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_COUNT)) {
            ps.setString(1, resourceId.value());
            try (ResultSet resultSet = ps.executeQuery()) {
                if (!resultSet.next()) {
                    return 0;
                }
                return resultSet.getInt("total");
            }
        } catch (SQLException e) {
            String message = String.format(
                    "Error counting resource definitions for resourceId=%s with message=%s",
                    resourceId.value(),
                    e.getMessage()
            );
            logger.severe(String.format(message, e));
            throw new DependencyException(message, e);
        }
    }
}
