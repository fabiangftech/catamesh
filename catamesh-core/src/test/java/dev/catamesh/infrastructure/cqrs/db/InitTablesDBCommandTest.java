package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.exception.AlreadyExistsException;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.infrastructure.cqrs.io.GetFileFromResourceQuery;
import dev.catamesh.support.H2TestSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class InitTablesDBCommandTest {

    @Test
    void executeInitializesSchema() throws Exception {
        DataSource dataSource = H2TestSupport.newDataSource();
        InitTablesDBCommand command = new InitTablesDBCommand(dataSource, new GetFileFromResourceQuery());

        Assertions.assertDoesNotThrow(() -> command.execute(null));
        Assertions.assertTrue(H2TestSupport.countRows(dataSource, "information_schema.tables") > 0);
    }

    @Test
    void executeMapsDuplicateSqlStateToAlreadyExistsException() {
        InitTablesDBCommand command = new InitTablesDBCommand(duplicateKeyDataSource(), path -> "ignored");

        AlreadyExistsException error = Assertions.assertThrows(AlreadyExistsException.class, () -> command.execute(null));

        Assertions.assertTrue(error.getMessage().contains("Duplicated resources"));
    }

    @Test
    void executeMapsUnexpectedLoaderErrorsToDependencyException() {
        DataSource dataSource = H2TestSupport.newDataSource();
        Query<String, String> brokenLoader = path -> {
            throw new IllegalStateException("boom");
        };
        InitTablesDBCommand command = new InitTablesDBCommand(dataSource, brokenLoader);

        DependencyException error = Assertions.assertThrows(DependencyException.class, () -> command.execute(null));

        Assertions.assertEquals("Unexpected error initializing Weave database schema", error.getMessage());
    }

    private DataSource duplicateKeyDataSource() {
        return new DataSource() {
            @Override
            public Connection getConnection() {
                return (Connection) Proxy.newProxyInstance(
                        Connection.class.getClassLoader(),
                        new Class<?>[]{Connection.class},
                        (proxy, method, args) -> switch (method.getName()) {
                            case "createStatement" -> duplicateKeyStatement();
                            case "close" -> null;
                            default -> unsupported(method.getName());
                        }
                );
            }

            @Override
            public Connection getConnection(String username, String password) {
                return getConnection();
            }

            @Override
            public <T> T unwrap(Class<T> iface) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isWrapperFor(Class<?> iface) {
                return false;
            }

            @Override
            public java.io.PrintWriter getLogWriter() {
                return null;
            }

            @Override
            public void setLogWriter(java.io.PrintWriter out) {
                // no-op
            }

            @Override
            public void setLoginTimeout(int seconds) {
                // no-op
            }

            @Override
            public int getLoginTimeout() {
                return 0;
            }

            @Override
            public java.util.logging.Logger getParentLogger() {
                return java.util.logging.Logger.getGlobal();
            }
        };
    }

    private Statement duplicateKeyStatement() {
        return (Statement) Proxy.newProxyInstance(
                Statement.class.getClassLoader(),
                new Class<?>[]{Statement.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "execute" -> throw new SQLException("duplicate", "23505");
                    case "close" -> null;
                    default -> unsupported(method.getName());
                }
        );
    }

    private Object unsupported(String methodName) {
        throw new UnsupportedOperationException(methodName);
    }
}
