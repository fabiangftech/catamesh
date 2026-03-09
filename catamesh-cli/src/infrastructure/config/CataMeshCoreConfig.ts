import path from "node:path";
import type {SpawnSyncOptionsWithStringEncoding, SpawnSyncReturns} from "node:child_process";

export type SpawnProcess = (
    command: string,
    args: readonly string[],
    options: SpawnSyncOptionsWithStringEncoding,
) => Pick<SpawnSyncReturns<string>, "error" | "signal" | "status" | "stdout" | "stderr">;

export class CataMeshCoreConfig {

    static CORE_JAR_PATH = path.resolve(__dirname, "../../core/catamesh-core.jar");
}