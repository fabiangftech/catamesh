import {TemplateFacade} from "../../core/facade/TemplateFacade";
import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import * as fs from "node:fs";

export class DefaultTemplateFacade implements TemplateFacade {

    private cataMeshCoreCommand: Query<string[], string> = new CataMeshCoreCommand();

    newTemplate(command: string[]): void {
        //todo validate input
        const yaml: string = this.cataMeshCoreCommand.execute(command);
        fs.writeFileSync(command[2] + ".yaml", yaml, "utf8");
    }
}