import {
    spawnSync,
} from "node:child_process";
import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreConfig, SpawnProcess} from "../config/CataMeshCoreConfig";

export class CataMeshCoreCommand implements Query<string[], string> {
    constructor(private readonly spawnProcess: SpawnProcess = spawnSync) {
    }

    execute(args: string[]): string {
        console.log(args)
        const child = this.spawnProcess(
            "java",
            ["-jar", CataMeshCoreConfig.CORE_JAR_PATH, args[0], ...args.slice(1)],
            {encoding: "utf8"},
        );
        console.log(child.status)
        if (child.status !== 0) {
            console.log(child.stderr)
            console.log(child.error)
            throw new Error(child.stderr || `Process exited with code ${child.status}`);
        }
        return child.stdout;
    }
}
