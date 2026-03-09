import {
    spawnSync,
} from "node:child_process";
import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreConfig, SpawnProcess} from "../config/CataMeshCoreConfig";
import {CataMeshCoreError} from "../../core/exception/CataMeshCoreError";

export class CataMeshCoreCommand implements Query<string[], string> {
    constructor(private readonly spawnProcess: SpawnProcess = spawnSync) {
    }

    execute(args: string[]): string {
        const child = this.spawnProcess(
            "java",
            ["-jar", CataMeshCoreConfig.CORE_JAR_PATH, args[0], ...args.slice(1)],
            {encoding: "utf8"},
        );
        if (child.status !== 0) {
            throw new CataMeshCoreError(child.status,child.stderr);
        }
        return child.stdout;
    }
}
