import {spawnSync} from "node:child_process";
import {CataMeshCoreConfig, SpawnProcess} from "../config/CataMeshCoreConfig";
import {Command} from "../../core/cqrs/Command";

export class CataMeshCoreCommand implements Command<string[], string> {
    constructor(private readonly spawnProcess: SpawnProcess = spawnSync) {
    }

    execute(args: string[]): string {
        const child = this.spawnProcess(
            "java",
            ["-jar", CataMeshCoreConfig.CORE_JAR_PATH, args[0], ...args.slice(1)],
            {encoding: "utf8"},
        );
        if (child.error) {
            throw child.error;
        }
        if (child.status !== 0) {
            return child.stderr;
        }
        return child.stdout;
    }
}
