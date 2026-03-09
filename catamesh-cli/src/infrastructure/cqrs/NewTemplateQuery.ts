import {
    spawnSync,
} from "node:child_process";
import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreConfig, SpawnProcess} from "../config/CataMeshCoreConfig";

export class NewTemplateQuery implements Query<string[], string> {
    constructor(private readonly spawnProcess: SpawnProcess = spawnSync) {
    }

    execute(args: string[]): string {
        const child = this.spawnProcess(
            "java",
            ["-jar", CataMeshCoreConfig.CORE_JAR_PATH, "new", ...args],
            {encoding: "utf8"},
        );
        return child.stdout;
    }
}
