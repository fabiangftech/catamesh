package dev.catamesh.infrastructure.adapter;


import dev.catamesh.core.exception.InvalidInputException;
import dev.catamesh.core.exception.MappingException;
import dev.catamesh.core.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class DataProductAdapter {

    private static final String COLUMN_ID = "id";
    private static final String COLUMNS_SCHEMA_VERSION = "schema_version";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DISPLAY_NAME = "display_name";
    private static final String COLUMN_DOMAIN = "domain";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_KIND = "kind";

    private DataProductAdapter() {
        // do nothing
    }

    public static DataProduct toDataProduct(ResultSet resultSet) throws SQLException {
        try {
            Metadata metadata = toMetadata(resultSet);
            Spec specification = toSpec(resultSet);
            SchemaVersion schemaVersion = SchemaVersion.fromValue(resultSet.getString(COLUMNS_SCHEMA_VERSION));
            return new DataProduct(schemaVersion, metadata, specification);
        } catch (InvalidInputException ex) {
            throw new MappingException("Invalid enum value in data_product row", ex);
        }
    }

    public static Metadata toMetadata(ResultSet resultSet) throws SQLException {
        return new Metadata(
                Key.create(resultSet.getString(COLUMN_ID)),
                resultSet.getString(COLUMN_NAME),
                resultSet.getString(COLUMN_DISPLAY_NAME),
                resultSet.getString(COLUMN_DOMAIN),
                resultSet.getString(COLUMN_DESCRIPTION)
        );
    }

    public static Spec toSpec(ResultSet resultSet) throws SQLException {
        try {
            return new Spec(DataProductKind.fromValue(resultSet.getString(COLUMN_KIND)), null);
        } catch (InvalidInputException ex) {
            throw new MappingException("Invalid data product kind in data_product row", ex);
        }
    }
}
