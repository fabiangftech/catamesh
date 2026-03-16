import path from "node:path";
import type {SpawnSyncOptionsWithStringEncoding, SpawnSyncReturns} from "node:child_process";

export type SpawnProcess = (
    command: string,
    args: readonly string[],
    options: SpawnSyncOptionsWithStringEncoding,
) => Pick<SpawnSyncReturns<string>, "error" | "signal" | "status" | "stdout" | "stderr">;

export class CataMeshCoreConfig {
    static PACKAGE_ROOT = path.resolve(__dirname, "../../..");
    static DB_DIR_ENV_VAR = "CATAMESH_DB_DIR";
    static DB_DIR_SYSTEM_PROPERTY = "catamesh.db.dir";
    static DEFAULT_DB_DIR = path.resolve(CataMeshCoreConfig.PACKAGE_ROOT, "db-file-catamesh");
    static CORE_JAR_PATH = path.resolve(__dirname, "../../core/catamesh-core.jar");

    static databaseDirectory(env: NodeJS.ProcessEnv = process.env): string {
        const configuredDirectory = env[CataMeshCoreConfig.DB_DIR_ENV_VAR]?.trim();

        if (configuredDirectory) {
            return path.resolve(configuredDirectory);
        }

        return CataMeshCoreConfig.DEFAULT_DB_DIR;
    }
}
