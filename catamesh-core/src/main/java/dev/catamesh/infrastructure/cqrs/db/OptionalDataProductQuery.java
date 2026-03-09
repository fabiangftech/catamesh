package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.model.DataProduct;
import dev.catamesh.infrastructure.adapter.DataProductAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class OptionalDataProductQuery implements Query<String, Optional<DataProduct>> {

    private static final Logger logger = Logger.getLogger(OptionalDataProductQuery.class.getName());

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

            logger.log(Level.SEVERE, message, e);

            throw new DependencyException(message);
        }
    }
}