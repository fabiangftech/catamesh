import {Facade} from "../../core/facade/Facade";
import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import {ColorConfig} from "../../infrastructure/config/ColorConfig";
import {ModelType} from "../../core/model/ModelType";

export class GetFacade implements Facade<string[], void> {
    private cataMeshCoreCommand: Query<string[], string> = new CataMeshCoreCommand();
    run(command: string[]): void {
        console.log(command)
        if (ModelType.data_product.includes(command[1])) {
            this.cataMeshCoreCommand.execute(command);
        } else {
            console.log(`${ColorConfig.white}Invalid type in ${command[1]}`);
        }
        return undefined;
    }
}