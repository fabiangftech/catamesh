package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.model.*;
import dev.catamesh.infrastructure.adapter.ResourceAdapter;
import dev.catamesh.infrastructure.dto.GetResourceDTO;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OptionalResourceQuery implements Query<GetResourceDTO, Optional<Resource>> {
    private static final Logger logger = Logger.getLogger(OptionalResourceQuery.class.getName());

    private static final String SQL_QUERY_BY_NAME_AND_DATA_PRODUCT_ID = """
                SELECT id, data_product_id, name, display_name, kind
                FROM resource
                WHERE data_product_id = ?
                and  name = ?
                limit 1
            """;

    private final DataSource dataSource;

    public OptionalResourceQuery(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Resource> execute(GetResourceDTO getResourceDTO) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_QUERY_BY_NAME_AND_DATA_PRODUCT_ID)) {
            ps.setString(1, getResourceDTO.dataProductId());
            ps.setString(2, getResourceDTO.name());

            try (ResultSet resultSet = ps.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(ResourceAdapter.toResource(resultSet));
            }
        } catch (SQLException e) {
            String message = String.format(
                    "Database error querying resource name=%s dataProductId=%s",
                    getResourceDTO.name(),
                    getResourceDTO.dataProductId()
            );

            logger.log(Level.SEVERE, message, e);
            throw new DependencyException(message);
        }
    }
}
