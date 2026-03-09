#!/usr/bin/env node

import * as fs from "node:fs";
import * as path from "node:path";
import {spawnSync, type SpawnSyncOptions, type SpawnSyncReturns} from "node:child_process";

export interface JavaInvocation {
  command: string;
  args: string[];
  jarPath: string;
}

type SpawnResult = Pick<SpawnSyncReturns<Buffer>, "error" | "signal" | "status">;
type SpawnProcess = (
  command: string,
  args: readonly string[],
  options: SpawnSyncOptions,
) => SpawnResult;

const SUCCESS = 0;
const FAILURE = 1;
const EMBEDDED_CORE_JAR_PATH = path.resolve(__dirname, "core", "catamesh-core-cli.jar");

export const printUsage = (stream: NodeJS.WriteStream = process.stdout): void => {
  stream.write("CataMesh CLI delegates subcommands to catamesh-core.\n");
  stream.write("Usage: cata <subcommand> [args]\n");
  stream.write("Example: cata init data-product my-first-data-product\n");
};

export const resolveJavaCommand = (env: NodeJS.ProcessEnv = process.env): string => {
  const javaBinary = process.platform === "win32" ? "java.exe" : "java";
  const javaHome = env.JAVA_HOME?.trim();

  if (!javaHome) {
    return javaBinary;
  }

  const javaFromHome = path.join(javaHome, "bin", javaBinary);
  if (fs.existsSync(javaFromHome)) {
    return javaFromHome;
  }

  return javaBinary;
};

export const resolveCoreJarPath = (env: NodeJS.ProcessEnv = process.env): string => {
  const overridePath = env.CATAMESH_CORE_JAR?.trim();
  if (overridePath) {
    return path.resolve(overridePath);
  }

  return EMBEDDED_CORE_JAR_PATH;
};

export const buildJavaInvocation = (
  argv: string[],
  env: NodeJS.ProcessEnv = process.env,
): JavaInvocation => {
  const jarPath = resolveCoreJarPath(env);
  return {
    command: resolveJavaCommand(env),
    args: ["-jar", jarPath, ...argv],
    jarPath,
  };
};

export const runCli = (
  argv: string[],
  env: NodeJS.ProcessEnv = process.env,
  spawnProcess: SpawnProcess = spawnSync,
): number => {
  if (argv.length === 0 || ["help", "-h", "--help"].includes(argv[0])) {
    printUsage(process.stdout);
    return SUCCESS;
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
