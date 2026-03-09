import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import * as fs from "node:fs";
import {Facade} from "../../core/facade/Facade";

export class TemplateFacade implements Facade<string[], void> {

    private cataMeshCoreCommand: Query<string[], string> = new CataMeshCoreCommand();

    run(command: string[]): void {
        //todo validate input
        const yaml: string = this.cataMeshCoreCommand.execute(command);
        fs.writeFileSync(command[2] + ".yaml", yaml, "utf8");
    }
}