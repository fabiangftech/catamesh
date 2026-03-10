package dev.catamesh.support;

import dev.catamesh.infrastructure.config.ApplicationConfig;
import dev.catamesh.infrastructure.cqrs.db.InitTablesDBCommand;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class H2TestSupport {
    private H2TestSupport() {
        // Utility class.
    }

    public static ApplicationConfig newApplicationConfig() {
        return new ApplicationConfig(newDataSource());
    }

    public static JdbcDataSource newDataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        dataSource.setUser("catamesh");
        dataSource.setPassword("catamesh");
        return dataSource;
    }

    public static void initSchema(DataSource dataSource) {
        new InitTablesDBCommand(dataSource, new GetFileFromResourceQuery()).execute(null);
    }

    public static void execute(DataSource dataSource, String sql) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error executing SQL for test setup", e);
        }
    }

    public static int countRows(DataSource dataSource, String table) {
        return queryInt(dataSource, "SELECT COUNT(*) FROM " + table);
    }

    public static int queryInt(DataSource dataSource, String sql, Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            bind(preparedStatement, params);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error querying integer for test setup", e);
        }
    }

    public static String queryString(DataSource dataSource, String sql, Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            bind(preparedStatement, params);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error querying string for test setup", e);
        }
    }

    public static void insertDataProduct(DataSource dataSource,
                                         String id,
                                         String schemaVersion,
                                         String name,
                                         String displayName,
                                         String kind,
                                         String domain,
                                         String description) {
        execute(
                dataSource,
                """
                INSERT INTO data_product (id, schema_version, name, display_name, kind, domain, description)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                id,
                schemaVersion,
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
                                      String kind) {
        execute(
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
                                                String schemaVersion,
                                                String version,
                                                boolean active,
                                                String configJson) {
        execute(
                dataSource,
                """
                INSERT INTO resource_definition (id, resource_id, schema_version, version, active, config)
                VALUES (?, ?, ?, ?, ?, ? FORMAT JSON)
                """,
                id,
                resourceId,
                schemaVersion,
                version,
                active,
                configJson
        );
    }

    private static void execute(DataSource dataSource, String sql, Object... params) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            bind(preparedStatement, params);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Error executing SQL for test setup", e);
        }
    }

    private static void bind(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int index = 0; index < params.length; index++) {
            preparedStatement.setObject(index + 1, params[index]);
        }
    }
}
