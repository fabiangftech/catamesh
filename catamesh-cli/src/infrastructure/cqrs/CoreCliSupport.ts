import * as fs from "node:fs";
import * as path from "node:path";

export interface JavaInvocation {
  command: string;
  args: string[];
  jarPath: string;
}

const EMBEDDED_CORE_JAR_PATH = path.resolve(
  __dirname,
  "..",
  "..",
  "core",
  "catamesh-core-cli.jar",
);

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
