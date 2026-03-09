package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Command;
import cl.guaman.weave.core.cqrs.Query;
import cl.guaman.weave.core.exception.AlreadyExistsException;
import cl.guaman.weave.core.exception.DependencyException;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Singleton
public class InitTablesDBCommand implements Command<Void, Void> {
    private static final Logger logger = LoggerFactory.getLogger(InitTablesDBCommand.class);
    private static final String SCHEMA_DB = "sql/000-schema.sql";
    private static final String DUPLICATE_KEY_SQL_STATE = "23505";
    private final DataSource dataSource;
    private final Query<String, String> getFileFromResourceQuery;

    public InitTablesDBCommand(DataSource dataSource,
                               Query<String, String> getFileFromResourceQuery) {
        this.dataSource = dataSource;
        this.getFileFromResourceQuery = getFileFromResourceQuery;
    }

    @Override
    public Void execute(Void input) {

        try (Connection conn = dataSource.getConnection();
             Statement statement = conn.createStatement()) {

            String sql = getFileFromResourceQuery.execute(SCHEMA_DB);
            statement.execute(sql);

        } catch (SQLException e) {

            if (DUPLICATE_KEY_SQL_STATE.equals(e.getSQLState())) {

                String message = """
                        Duplicated resources detected for the same data product and name.
                        Clean duplicated rows before starting Weave.
                        """;

                logger.error(message, e);

                throw new AlreadyExistsException(message);
            }

            String message = "Database error initializing Weave schema";

            logger.error(message, e);

            throw new DependencyException(message);

        } catch (Exception e) {

            String message = "Unexpected error initializing Weave database schema";

            logger.error(message, e);

            throw new DependencyException(message);
        }

        return null;
    }
}