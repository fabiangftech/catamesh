import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import {readFile, writeFile} from "fs/promises";
import {Query} from "../../core/cqrs/Query";
import {GetNameFileYMLQuery} from "../../infrastructure/cqrs/GetNameFileYMLQuery";
import {Command} from "../../core/cqrs/Command";
import {GetKindFromContentQuery} from "../../infrastructure/cqrs/GetKindFromContentQuery";

export class DefaultCataMeshFacade implements CataMeshFacade {

    constructor(private cataMeshCoreCommand: Command<string[], string> = new CataMeshCoreCommand(),
                private getNameFileYMLQuery: Query<string, Promise<string>> = new GetNameFileYMLQuery(),
                private getKindFromContentQuery: Query<string, string> = new GetKindFromContentQuery()) {
    }

    async init(command: string[]): Promise<void> {
        const fileName: string = command[2];
        const result: string = this.cataMeshCoreCommand.execute(command);
        await writeFile(fileName + ".yaml", result, "utf8");
    }

    async diff(command: string[]): Promise<void> {
        await this.execute(command)
    }

    async plan(command: string[]): Promise<void> {
        await this.execute(command)
    }

    async apply(command: string[]): Promise<void> {
        await this.execute(command)
    }

    async get(command: string[]): Promise<void> {
        const result: string = this.cataMeshCoreCommand.execute(command);
        console.log(result);
    }

    async pull(command: string[]): Promise<void> {
        const result: string = this.cataMeshCoreCommand.execute(command);
        const fileName: string = command[2] + ".yaml";
        await writeFile(fileName, result, "utf8");
        console.info(`Successfully pulled '${command[2]}' to ${fileName}`);
    }

    private async execute(command: string[]): Promise<void> {
        let fileName: string = command[1];
        fileName = await this.getNameFileYMLQuery.execute(fileName);
        const content: string = await readFile(fileName, "utf8");
        command[1] = this.getKindFromContentQuery.execute(content);
        command[2] = content;
        const result: string = this.cataMeshCoreCommand.execute(command);
        console.log(result)
    }
}