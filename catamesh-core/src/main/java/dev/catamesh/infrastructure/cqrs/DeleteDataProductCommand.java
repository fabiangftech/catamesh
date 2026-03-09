package dev.catamesh.infrastructure.cqrs;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.DependencyException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteDataProductCommand implements Command<String, Void> {

    private static final Logger logger = Logger.getLogger(DeleteDataProductCommand.class.getName());

    private static final String SQL_DELETE = """
            DELETE FROM data_product
            WHERE name = ?
            """;

    private final DataSource dataSource;

    public DeleteDataProductCommand(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Void execute(String name) {

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_DELETE)) {

            preparedStatement.setString(1, name);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {

            String message = String.format(
                    "Database error deleting data product (name=%s)",
                    name
            );

            logger.log(Level.SEVERE, message, e);

            throw new DependencyException(message);
        }

        return null;
    }
}