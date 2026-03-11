import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import * as fs from "node:fs";
import {Command} from "../../core/cqrs/Command";
import {Facade} from "../../core/facade/Facade";
import {DiffPrintCommand} from "../../infrastructure/cqrs/DiffPrintCommand";
import {assertDataProductSchema, resolveYamlFileName} from "./DataProductYamlSupport";
import {DiffResult} from "../../core/model/DiffResult";

export class DiffFacade implements Facade<string[], void> {
    private cataMeshCoreCommand: Query<string[], string> = new CataMeshCoreCommand();
    private diffPrintCommand: Command<DiffResult, void> = new DiffPrintCommand();

    run(command: string[]): void {
        const fileName = resolveYamlFileName(command[1]);
        //todo check if file exist
        const yaml = fs.readFileSync(fileName, "utf8");
        assertDataProductSchema("diff", fileName, yaml);
        this.diffDataProduct(command, yaml);
    }

    private diffDataProduct(command: string[], yaml: string): void {
        command[1] = yaml;
        const diff: DiffResult = JSON.parse(this.cataMeshCoreCommand.execute(command));
        this.diffPrintCommand.execute(diff);
    }
}
