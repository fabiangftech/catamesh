import {Query} from "../../core/cqrs/Query";
import {access} from "fs/promises";

export class GetNameFileYMLQuery implements Query<string, Promise<string> > {
    async execute(name: string): Promise<string> {
        let nameFile: string = name
        if (await this.fileExists(nameFile + ".yml")) {
            return nameFile + ".yml";
        } else if (await this.fileExists(nameFile + ".yaml")) {
            return nameFile + ".yaml";
        } else {
            throw new Error("file not found!")
        }
    }

    async fileExists(path: string): Promise<boolean> {
        try {
            await access(path);
            return true;
        } catch {
            return false;
        }
    }
}