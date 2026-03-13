import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import {readFile, writeFile} from "fs/promises";
import {access} from "fs/promises";
import {Query} from "../../core/cqrs/Query";
import {GetNameFileYMLQuery} from "../../infrastructure/cqrs/GetNameFileYMLQuery";
import {Command} from "../../core/cqrs/Command";

export class DefaultCataMeshFacade implements CataMeshFacade {

    constructor(private cataMeshCoreCommand:  Command<string[], string> = new CataMeshCoreCommand(),
                private getNameFileYMLQuery: Query<string, Promise<string>> = new GetNameFileYMLQuery()) {
    }

    async init(command: string[]): Promise<void> {
        const nameFile: string = command[2];
        const result: string = this.cataMeshCoreCommand.execute(command);
        await writeFile(nameFile + ".yaml", result, "utf8");
    }

    async diff(command: string[]): Promise<void> {
        let nameFile: string = command[1];
        nameFile = await this.getNameFileYMLQuery.execute(nameFile);
        const content: string = await readFile(nameFile, "utf8");
        command[1] = "data-product";
        command[2] = content;
        const result: string = this.cataMeshCoreCommand.execute(command);
        console.log(result)
    }

    plan(command: string[]): void {
        this.cataMeshCoreCommand.execute(command)
    }

    apply(command: string[]): void {

    }
}