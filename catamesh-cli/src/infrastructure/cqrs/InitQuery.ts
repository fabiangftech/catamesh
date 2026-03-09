import * as fs from "node:fs";
import {
  spawnSync,
  type SpawnSyncOptionsWithStringEncoding,
  type SpawnSyncReturns,
} from "node:child_process";

import {Query} from "../../core/cqrs/Query";
import {buildJavaInvocation} from "./CoreCliSupport";

export type InitQueryInput = string[];

type SpawnProcess = (
  command: string,
  args: readonly string[],
  options: SpawnSyncOptionsWithStringEncoding,
) => Pick<SpawnSyncReturns<string>, "error" | "signal" | "status" | "stdout" | "stderr">;

export class InitQuery implements Query<InitQueryInput, string> {
  constructor(
    private readonly env: NodeJS.ProcessEnv = process.env,
    private readonly spawnProcess: SpawnProcess = spawnSync,
  ) {}

  execute(input: InitQueryInput): string {
    const invocation = buildJavaInvocation(["init", ...input], this.env);

    if (!fs.existsSync(invocation.jarPath)) {
      throw new Error(`CataMesh core JAR not found: ${invocation.jarPath}`);
    }

    const child = this.spawnProcess(invocation.command, invocation.args, {
      encoding: "utf8",
      env: this.env,
    });

    if (child.error) {
      throw new Error(`Failed to start Java runtime: ${child.error.message}`);
    }

    if (child.status === null) {
      if (child.signal) {
        throw new Error(`catamesh-core terminated by signal: ${child.signal}`);
      }

      throw new Error("catamesh-core init terminated unexpectedly.");
    }

    if (child.status !== 0) {
      const message = child.stderr.trim();
      if (message) {
        throw new Error(message);
      }

      throw new Error(`catamesh-core init failed with exit code ${child.status}.`);
    }

    return child.stdout;
  }
}
