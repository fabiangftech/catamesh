package dev.catamesh.infrastructure.cqrs;

import cl.guaman.weave.core.cqrs.Command;
import cl.guaman.weave.core.exception.DependencyException;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Singleton
@Named("deleteDataProductCommand")
public class DeleteDataProductCommand implements Command<String, Void> {

    private static final Logger logger = LoggerFactory.getLogger(DeleteDataProductCommand.class);

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

            logger.error(message, e);

            throw new DependencyException(message);
        }

        return null;
    }
}