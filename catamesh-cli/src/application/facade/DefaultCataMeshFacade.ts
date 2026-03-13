import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import {readFile, writeFile} from "fs/promises";

export class DefaultCataMeshFacade implements CataMeshFacade {

    constructor(private cataMeshCoreCommand: CataMeshCoreCommand = new CataMeshCoreCommand()) {
    }

    async init(command: string[]): Promise<void> {
        const nameFile: string = command[2];
        const result: string = this.cataMeshCoreCommand.execute(command);
        await writeFile(nameFile + ".yaml", result, "utf8");
    }

    async diff(command: string[]): Promise<void> {
        const nameFile: string = command[1];
        const content: string = await readFile(nameFile + ".yaml", "utf8");
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