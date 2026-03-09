import { spawnSync } from "node:child_process";
import * as path from "node:path";

export class InitQuery {
  execute(args: string[]) {
    return spawnSync(
        "java",
        ["-jar", path.resolve(__dirname, "../../core/catamesh-core.jar"), "init", ...args],
        { encoding: "utf8" }
    ).stdout;
  }
}