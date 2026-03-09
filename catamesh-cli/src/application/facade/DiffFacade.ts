import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import * as fs from "node:fs";
import {Command} from "../../core/cqrs/Command";
import {Schema} from "../../core/model/Schema";
import {ColorConfig} from "../../infrastructure/config/ColorConfig";
import {Facade} from "../../core/facade/Facade";
import {Diff} from "../../core/model/Diff";
import {DiffPrintCommand} from "../../infrastructure/cqrs/DiffPrintCommand";

export class DiffFacade implements Facade<string[], void> {
    private cataMeshCoreCommand: Query<string[], string> = new CataMeshCoreCommand();
    private diffPrintCommand: Command<Diff, void> = new DiffPrintCommand();

    run(command: string[]): void {
        const fileName: string = command[1].includes(".yml") ? command[1] : command[1] + ".yaml";
        console.log(fileName)
        //todo check if file exist
        const yaml: string = fs.readFileSync(fileName, "utf8");
        if (yaml.includes(Schema.data_product_v1)) {
            this.diffDataProduct(command, yaml);
        } else {
            console.log(`${ColorConfig.white}Invalid schema version in ${command[1]}.yaml`);
        }
    }

    private diffDataProduct(command: string[], yaml: string): void {
        command[1] = yaml;
        const diff: Diff = JSON.parse(this.cataMeshCoreCommand.execute(command));
        this.diffPrintCommand.execute(diff);
    }
}