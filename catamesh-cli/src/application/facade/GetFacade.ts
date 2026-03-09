import {Facade} from "../../core/facade/Facade";
import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import {ColorConfig} from "../../infrastructure/config/ColorConfig";
import {ModelType} from "../../core/model/ModelType";
import {DataProduct} from "../../core/model/DataProduct";
import {DataProductPrintCommand} from "../../infrastructure/cqrs/DataProductPrintCommand";
import {Command} from "../../core/cqrs/Command";

export class GetFacade implements Facade<string[], void> {

    private cataMeshCoreCommand: Query<string[], string> = new CataMeshCoreCommand();
    private dataProductPrintCommand: Command<DataProduct, void> = new DataProductPrintCommand();

    run(command: string[]): void {
        console.log(command)
        if (ModelType.data_product.includes(command[1])) {
            const dataProduct: DataProduct = JSON.parse(this.cataMeshCoreCommand.execute(command));
            this.dataProductPrintCommand.execute(dataProduct);
        } else {
            console.log(`${ColorConfig.white}Invalid type in ${command[1]}`);
        }
        return undefined;
    }
}