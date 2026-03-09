import {
  spawnSync,
  type SpawnSyncOptionsWithStringEncoding,
  type SpawnSyncReturns,
} from "node:child_process";
import * as path from "node:path";

type SpawnProcess = (
  command: string,
  args: readonly string[],
  options: SpawnSyncOptionsWithStringEncoding,
) => Pick<SpawnSyncReturns<string>, "error" | "signal" | "status" | "stdout" | "stderr">;

const CORE_JAR_PATH = path.resolve(__dirname, "../../core/catamesh-core-cli.jar");

export class InitQuery {
  constructor(
    private readonly spawnProcess: SpawnProcess = spawnSync,
  ) {}

  execute(args: string[]): string {
    const child = this.spawnProcess(
      "java",
      ["-jar", CORE_JAR_PATH, "new", ...args],
      {encoding: "utf8"},
    );
    return child.stdout;
  }
}
