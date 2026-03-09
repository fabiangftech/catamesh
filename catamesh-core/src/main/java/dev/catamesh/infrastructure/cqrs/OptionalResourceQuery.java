package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Query;
import cl.guaman.weave.core.exception.DependencyException;
import cl.guaman.weave.core.model.*;
import cl.guaman.weave.infrastructure.adapter.ResourceAdapter;
import cl.guaman.weave.infrastructure.dto.GetResourceDTO;
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
public class OptionalResourceQuery implements Query<GetResourceDTO, Optional<Resource>> {
    private static final Logger logger = LoggerFactory.getLogger(OptionalResourceQuery.class);

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

            logger.error(message, e);
            throw new DependencyException(message);
        }
    }
}
