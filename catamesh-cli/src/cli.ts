#!/usr/bin/env node

import * as fs from "node:fs";
import {spawnSync, type SpawnSyncOptions, type SpawnSyncReturns} from "node:child_process";

import {buildJavaInvocation} from "./infrastructure/cqrs/CoreCliSupport";
import {InitQuery, type InitQueryInput} from "./infrastructure/cqrs/InitQuery";

type SpawnResult = Pick<SpawnSyncReturns<Buffer>, "error" | "signal" | "status">;
type SpawnProcess = (
  command: string,
  args: readonly string[],
  options: SpawnSyncOptions,
) => SpawnResult;

interface InitCommandQuery {
  execute(input: InitQueryInput): string;
}

const SUCCESS = 0;
const FAILURE = 1;

export const printUsage = (stream: NodeJS.WriteStream = process.stdout): void => {
  stream.write("CataMesh CLI delegates subcommands to catamesh-core.\n");
  stream.write("Usage: cata <subcommand> [args]\n");
  stream.write("Example: cata init data-product my-first-data-product\n");
};

export const runCli = (
  argv: string[],
  env: NodeJS.ProcessEnv = process.env,
  spawnProcess: SpawnProcess = spawnSync,
  initQuery: InitCommandQuery = new InitQuery(env),
): number => {
  if (argv.length === 0 || ["help", "-h", "--help"].includes(argv[0])) {
    printUsage(process.stdout);
    return SUCCESS;
  }

  if (argv[0] === "init") {
    try {
      const output = initQuery.execute(argv.slice(1));
      process.stdout.write(output);
      return SUCCESS;
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      process.stderr.write(message.endsWith("\n") ? message : `${message}\n`);
      return FAILURE;
    }
  }

  const invocation = buildJavaInvocation(argv, env);
  if (!fs.existsSync(invocation.jarPath)) {
    process.stderr.write(`CataMesh core JAR not found: ${invocation.jarPath}\n`);
    return FAILURE;
  }

  const child = spawnProcess(invocation.command, invocation.args, {
    env,
    stdio: "inherit",
  });

  if (child.error) {
    process.stderr.write(`Failed to start Java runtime: ${child.error.message}\n`);
    return FAILURE;
  }

  if (child.status === null) {
    if (child.signal) {
      process.stderr.write(`catamesh-core terminated by signal: ${child.signal}\n`);
    }
    return FAILURE;
  }

  return child.status;
};

if (require.main === module) {
  process.exit(runCli(process.argv.slice(2)));
}
