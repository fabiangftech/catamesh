import {Facade} from "../../core/facade/Facade";
import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import {Plan} from "../../core/model/Plan";
import fs from "node:fs";
import {Schema} from "../../core/model/Schema";
import {ColorConfig} from "../../infrastructure/config/ColorConfig";

export class ApplyFacade implements Facade<string[], void> {
    private cataMeshCoreCommand: Query<string[], string> = new CataMeshCoreCommand();

    run(command: string[]): void {
        const fileName: string = command[1].includes(".yml") ? command[1] : command[1] + ".yaml";
        //todo check if file exist
        const yaml: string = fs.readFileSync(fileName, "utf8");
        if (yaml.includes(Schema.data_product_v1)) {
            this.applyDataProduct(command, yaml);
        } else {
            console.log(`${ColorConfig.white}Invalid schema version in ${command[1]}.yaml`);
        }
        return undefined;
    }

    private applyDataProduct(command: string[], yaml: string): void {
        command[1] = yaml;
        const apply: string = this.cataMeshCoreCommand.execute(command);
        console.log(`${ColorConfig.white}${apply}`)
    }
}