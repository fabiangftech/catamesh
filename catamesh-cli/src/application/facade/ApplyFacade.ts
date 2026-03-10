import {Facade} from "../../core/facade/Facade";
import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import fs from "node:fs";
import {ApplyResult} from "../../core/model/ApplyResult";
import {Command} from "../../core/cqrs/Command";
import {ApplyPrintCommand} from "../../infrastructure/cqrs/ApplyPrintCommand";
import {assertDataProductSchema, resolveYamlFileName} from "./DataProductYamlSupport";

export class ApplyFacade implements Facade<string[], void> {
    private cataMeshCoreCommand: Query<string[], string> = new CataMeshCoreCommand();
    private applyPrintCommand: Command<ApplyResult, void> = new ApplyPrintCommand();

    run(command: string[]): void {
        const fileName = resolveYamlFileName(command[1]);
        //todo check if file exist
        const yaml = fs.readFileSync(fileName, "utf8");
        assertDataProductSchema("apply", fileName, yaml);
        this.applyDataProduct(command, yaml);
        return undefined;
    }

    private applyDataProduct(command: string[], yaml: string): void {
        command[1] = yaml;
        const applyResult: ApplyResult = JSON.parse(this.cataMeshCoreCommand.execute(command));
        this.applyPrintCommand.execute(applyResult);
    }
}
