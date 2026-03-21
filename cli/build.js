#!/usr/bin/env node

const fs = require("node:fs");
const path = require("node:path");
const {spawnSync} = require("node:child_process");

const cliDir = __dirname;
const repoDir = path.resolve(cliDir, "..");
const coreDir = path.join(repoDir, "core");
const distDir = path.join(cliDir, "dist");
const embeddedCoreDir = path.join(distDir, "core");
const coreJarPath = path.join(coreDir, "build", "libs", "catamesh-core.jar");

const run = (command, args, options = {}) => {
  const result = spawnSync(command, args, {
    stdio: "inherit",
    ...options,
  });

  if (result.error) {
    throw result.error;
  }

  if (result.status !== 0) {
    process.exit(result.status ?? 1);
  }
};

const resolveGradleCommand = () => (
  process.platform === "win32" ? "gradlew.bat" : "./gradlew"
);

const createGradleEnv = () => {
  const env = {...process.env};
  const javaHome = env.JAVA_HOME?.trim();

  if (!javaHome) {
    return env;
  }

  env.JAVA_HOME = javaHome;
  env.PATH = `${path.join(javaHome, "bin")}${path.delimiter}${env.PATH ?? ""}`;
  return env;
};

const compileTypeScript = () => {
  const tscEntrypoint = require.resolve("typescript/bin/tsc", {paths: [cliDir]});
  run(process.execPath, [tscEntrypoint, "-p", "tsconfig.json"], {cwd: cliDir});
};

fs.rmSync(distDir, {recursive: true, force: true});

run(resolveGradleCommand(), ["fatJar"], {
  cwd: coreDir,
  env: createGradleEnv(),
});
compileTypeScript();

if (!fs.existsSync(coreJarPath)) {
  throw new Error(`Missing core jar after build: ${coreJarPath}`);
}

fs.mkdirSync(embeddedCoreDir, {recursive: true});
fs.copyFileSync(coreJarPath, path.join(embeddedCoreDir, "catamesh-core.jar"));
fs.chmodSync(path.join(distDir, "cli.js"), 0o755);
