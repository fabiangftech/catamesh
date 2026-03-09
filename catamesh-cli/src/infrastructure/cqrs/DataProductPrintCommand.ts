import {Command} from "../../core/cqrs/Command";
import {DataProduct} from "../../core/model/DataProduct";

export class DataProductPrintCommand implements Command<DataProduct, void>{
    execute(input: DataProduct): void {
        return undefined;
    }
}