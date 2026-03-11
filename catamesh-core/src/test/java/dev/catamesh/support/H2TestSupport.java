package dev.catamesh.support;

import dev.catamesh.infrastructure.config.ApplicationConfig;
import org.h2.jdbcx.JdbcDataSource;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.UUID;

public final class H2TestSupport {

    private H2TestSupport() {
    }

    public static DataSource newDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        dataSource.setUser("catamesh");
        dataSource.setPassword("catamesh");
        return dataSource;
    }

    public static ApplicationConfig newApplicationConfig() {
        return new ApplicationConfig(newDataSource());
    }

    public static void insertDataProduct(DataSource dataSource,
                                         String id,
                                         String name,
                                         String displayName,
                                         String kind,
                                         String domain,
                                         String description) throws SQLException {
        executeUpdate(
                dataSource,
                """
                INSERT INTO data_product (id, schema_version, name, display_name, kind, domain, description)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                "data-product/v1",
                name,
                displayName,
                kind,
                domain,
                description
        );
    }

    public static void insertResource(DataSource dataSource,
                                      String id,
                                      String dataProductId,
                                      String name,
                                      String displayName,
                                      String kind) throws SQLException {
        executeUpdate(
                dataSource,
                """
                INSERT INTO resource (id, data_product_id, name, display_name, kind)
                VALUES (?, ?, ?, ?, ?)
                """,
                id,
                dataProductId,
                name,
                displayName,
                kind
        );
    }

    public static void insertResourceDefinition(DataSource dataSource,
                                                String id,
                                                String resourceId,
                                                String version,
                                                boolean active,
                                                Map<String, Object> config) throws Exception {
        executeUpdate(
                dataSource,
                """
                INSERT INTO resource_definition (id, resource_id, schema_version, version, active, config)
                VALUES (?, ?, ?, ?, ?, ? FORMAT JSON)
                """,
                id,
                resourceId,
                "bucket/v1",
                version,
                active,
                new ObjectMapper().writeValueAsString(config)
        );
    }

    public static void insertResourceDefinitionRaw(DataSource dataSource,
                                                   String id,
                                                   String resourceId,
                                                   String version,
                                                   boolean active,
                                                   String configJson) throws SQLException {
        executeUpdate(
                dataSource,
                """
                INSERT INTO resource_definition (id, resource_id, schema_version, version, active, config)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                id,
                resourceId,
                "bucket/v1",
                version,
                active,
                configJson
        );
    }

    public static int countRows(DataSource dataSource, String tableName) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             java.sql.ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS total FROM " + tableName)) {
            resultSet.next();
            return resultSet.getInt("total");
        }
    }

    public static void executeSql(DataSource dataSource, String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private static void executeUpdate(DataSource dataSource, String sql, Object... params) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                preparedStatement.setObject(i + 1, params[i]);
            }
            preparedStatement.executeUpdate();
        }
    }
}
