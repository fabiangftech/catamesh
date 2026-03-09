package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Query;
import cl.guaman.weave.core.exception.DependencyException;
import cl.guaman.weave.core.model.DataProduct;
import cl.guaman.weave.infrastructure.adapter.DataProductAdapter;
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
public class OptionalDataProductQuery implements Query<String, Optional<DataProduct>> {

    private static final Logger logger = LoggerFactory.getLogger(OptionalDataProductQuery.class);

    private static final String SQL_QUERY_BY_NAME = """
            SELECT id, schema_version, name, display_name, kind, domain, description
            FROM data_product
            WHERE name = ?
            """;

    private final DataSource dataSource;

    public OptionalDataProductQuery(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<DataProduct> execute(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_QUERY_BY_NAME)) {

            ps.setString(1, name);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(DataProductAdapter.toDataProduct(resultSet));
            }

        } catch (SQLException e) {
            String message = String.format(
                    "Database error querying data product (name=%s)",
                    name
            );

            logger.error(message, e);

            throw new DependencyException(message);
        }
    }
}