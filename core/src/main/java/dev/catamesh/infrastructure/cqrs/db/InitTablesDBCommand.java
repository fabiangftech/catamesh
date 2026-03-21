package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.exception.AlreadyExistsException;
import dev.catamesh.core.exception.DependencyException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InitTablesDBCommand implements Command<Void, Void> {
    private static final Logger logger = Logger.getLogger(InitTablesDBCommand.class.getName());
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

                logger.log(Level.SEVERE, message, e);

                throw new AlreadyExistsException(message);
            }

            String message = "Database error initializing Weave schema";

            logger.log(Level.SEVERE, message, e);

            throw new DependencyException(message);

        } catch (Exception e) {

            String message = "Unexpected error initializing Weave database schema";

            logger.log(Level.SEVERE, message, e);

            throw new DependencyException(message);
        }

        return null;
    }
}