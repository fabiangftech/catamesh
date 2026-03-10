import {Schema} from "../../core/model/Schema";
import {invalidDataProductSchema} from "../../core/exception/CataMeshCliValidationErrors";

export function resolveYamlFileName(input: string): string {
    if (input.endsWith(".yaml") || input.endsWith(".yml")) {
        return input;
    }
    return `${input}.yaml`;
}

export function assertDataProductSchema(commandName: string, fileName: string, yaml: string): void {
    if (!yaml.includes(Schema.data_product_v1)) {
        throw invalidDataProductSchema(commandName, fileName);
    }
}
