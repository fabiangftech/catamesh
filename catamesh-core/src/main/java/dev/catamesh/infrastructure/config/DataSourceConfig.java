package dev.catamesh.infrastructure.config;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public final class DataSourceConfig {
    private static final String CATAMESH = "catamesh";
    public static final String DB_DIR_SYSTEM_PROPERTY = "catamesh.db.dir";
    public static final String DB_DIR_ENV_VAR = "CATAMESH_DB_DIR";
    public static final String DEFAULT_DB_DIR = "./db-file-catamesh";
    private static final String H2_URL_SUFFIX = ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1";

    private static final JdbcDataSource localJdbcDataSource;

    private DataSourceConfig() {
        // do nothing
    }

    static {
        localJdbcDataSource = new JdbcDataSource();
        localJdbcDataSource.setURL(resolveDatabaseUrl());
        localJdbcDataSource.setUser(CATAMESH);
        localJdbcDataSource.setPassword("");
    }

    public static DataSource get() {
        return localJdbcDataSource;
    }

    private static String resolveDatabaseUrl() {
        return resolveDatabaseUrl(System.getProperties(), System.getenv());
    }

    private static String resolveDatabaseUrl(Properties systemProperties, Map<String, String> environment) {
        return resolveDatabaseUrl(resolveDatabaseDirectory(systemProperties, environment));
    }

    private static String resolveDatabaseUrl(String databaseDirectory) {
        Path databasePath = Path.of(databaseDirectory)
                .toAbsolutePath()
                .normalize()
                .resolve("catamesh_db");
        return "jdbc:h2:file:" + databasePath.toString().replace('\\', '/') + H2_URL_SUFFIX;
    }

    private static String resolveDatabaseDirectory(Properties properties, Map<String, String> environment) {
        String propertyDirectory = properties.getProperty(DB_DIR_SYSTEM_PROPERTY);
        if (propertyDirectory != null) {
            return propertyDirectory;
        }
        String environmentDirectory = environment.get(DB_DIR_ENV_VAR);
        return Objects.requireNonNullElse(environmentDirectory, DEFAULT_DB_DIR);
    }
}
