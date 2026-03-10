import {CataMeshCliError} from "./CataMeshCliError";
import {ModelType} from "../model/ModelType";
import {Schema} from "../model/Schema";

const VALIDATION_STATUS = 20;
const VALIDATION_CODE = "VALIDATION_ERROR";

export function invalidDataProductSchema(commandName: string, fileName: string): CataMeshCliError {
    return new CataMeshCliError(
        VALIDATION_STATUS,
        `File ${fileName} is not supported by cata ${commandName}.`,
        VALIDATION_CODE,
        "Invalid schema version",
        `Use a YAML with schemaVersion: ${Schema.data_product_v1}.`,
        [`supported schemaVersion: ${Schema.data_product_v1}`],
    );
}

export function invalidGetModelType(rawType: string): CataMeshCliError {
    return new CataMeshCliError(
        VALIDATION_STATUS,
        `Type ${rawType} is not supported by cata get.`,
        VALIDATION_CODE,
        "Invalid model type",
        `Use ${ModelType.data_product}.`,
        [`supported types: ${ModelType.data_product}`],
    );
}
