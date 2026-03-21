package dev.catamesh.infrastructure.cqrs.db;

import dev.catamesh.core.cqrs.Command;
import dev.catamesh.core.exception.DependencyException;
import dev.catamesh.core.exception.InvariantException;
import dev.catamesh.core.model.DataProduct;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdateDataProductCommand implements Command<DataProduct, DataProduct> {
    private static final Logger logger = Logger.getLogger(UpdateDataProductCommand.class.getName());
    private static final String SQL_UPDATE = """
            UPDATE data_product
            SET display_name = ?, domain = ?, description = ?
            WHERE id = ?
            """;
    private final DataSource dataSource;

    public UpdateDataProductCommand(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataProduct execute(DataProduct dataProduct) {
        if (Objects.isNull(dataProduct.getMetadata().getId())) {
            throw new InvariantException(
                    String.format("Data product id is required to update data product=%s", dataProduct.getMetadata().getName())
            );
        }

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, dataProduct.getMetadata().getDisplayName());
            preparedStatement.setString(2, dataProduct.getMetadata().getDomain());
            preparedStatement.setString(3, dataProduct.getMetadata().getDescription());
            preparedStatement.setString(4, dataProduct.getMetadata().getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String message = String.format("Error updating data product(name=%s)", dataProduct.getMetadata().getName());
            logger.log(Level.SEVERE, message, e);
            throw new DependencyException(message);
        }
        return dataProduct;
    }
}
