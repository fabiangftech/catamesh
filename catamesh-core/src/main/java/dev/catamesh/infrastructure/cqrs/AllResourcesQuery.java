package dev.catamesh.infrastructure.cqrs;


import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.model.Resource;
import dev.catamesh.infrastructure.adapter.ResourceAdapter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AllResourcesQuery implements Query<String, List<Resource>> {
    private static final Logger logger = Logger.getLogger(AllResourcesQuery.class.getName());
    private static final String SQL_QUERY_ALL_BY_DATA_PRODUCT_NAME = """
            SELECT r.id, r.data_product_id, r.name, r.display_name, r.kind
            FROM resource r
            JOIN data_product dp ON dp.id = r.data_product_id
            WHERE dp.name = ?
            ORDER BY r.name ASC
            """;

    private final DataSource dataSource;

    public AllResourcesQuery(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Resource> execute(String dataProductName) {
        List<Resource> resources = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SQL_QUERY_ALL_BY_DATA_PRODUCT_NAME)) {
            ps.setString(1, dataProductName);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    resources.add(ResourceAdapter.toResource(resultSet));
                }
            }
            return resources;
        } catch (SQLException e) {
            String message = String.format("Error getting all resources for data product=%s with message=%s",
                    dataProductName,
                    e.getMessage());
            logger.severe(String.format(message, e));
            throw new DependencyException(message, e);
        }
    }
}
