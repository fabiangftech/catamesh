const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const os = require("node:os");
const path = require("node:path");

const cli = require("../dist/cli.js");
const coreCliSupport = require("../dist/infrastructure/cqrs/CoreCliSupport.js");

const captureWrites = (stream) => {
  const writes = [];
  const originalWrite = stream.write;

  stream.write = (chunk, encoding, callback) => {
    writes.push(String(chunk));

    if (typeof encoding === "function") {
      encoding();
    }

    if (typeof callback === "function") {
      callback();
    }

    return true;
  };

  return {
    output: () => writes.join(""),
    restore: () => {
      stream.write = originalWrite;
    },
  };
};

test("buildJavaInvocation uses embedded jar by default", () => {
  const invocation = coreCliSupport.buildJavaInvocation(["plan"], {});

  assert.equal(invocation.command, process.platform === "win32" ? "java.exe" : "java");
  assert.deepEqual(invocation.args, ["-jar", invocation.jarPath, "plan"]);
  assert.equal(
    invocation.jarPath,
    path.resolve(__dirname, "..", "dist", "core", "catamesh-core-cli.jar"),
  );
});

test("buildJavaInvocation prefers CATAMESH_CORE_JAR override", () => {
  const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), "catamesh-cli-"));
  const overrideJar = path.join(tmpDir, "override.jar");
  fs.writeFileSync(overrideJar, "");

  const invocation = coreCliSupport.buildJavaInvocation(["diff"], {
    CATAMESH_CORE_JAR: overrideJar,
  });

  assert.equal(invocation.jarPath, overrideJar);
});

test("resolveJavaCommand prefers JAVA_HOME when java binary exists", () => {
  const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), "catamesh-java-"));
  const binDir = path.join(tmpDir, "bin");
  const javaBinary = process.platform === "win32" ? "java.exe" : "java";
  const javaPath = path.join(binDir, javaBinary);
  fs.mkdirSync(binDir, {recursive: true});
  fs.writeFileSync(javaPath, "");

  assert.equal(coreCliSupport.resolveJavaCommand({JAVA_HOME: tmpDir}), javaPath);
});

test("runCli routes init through InitQuery and prints returned output", () => {
  const stdout = captureWrites(process.stdout);
  const stderr = captureWrites(process.stderr);

  try {
    const initQuery = {
      execute(input) {
        assert.deepEqual(input, ["data-product", "mesh"]);
        return "schemaVersion: data-product/v1\n";
      },
    };

    const exitCode = cli.runCli(["init", "data-product", "mesh"], {}, () => {
      throw new Error("generic spawn should not run for init");
    }, initQuery);

    assert.equal(exitCode, 0);
    assert.equal(stdout.output(), "schemaVersion: data-product/v1\n");
    assert.equal(stderr.output(), "");
  } finally {
    stdout.restore();
    stderr.restore();
  }
});

test("runCli maps InitQuery errors to stderr and exit code 1", () => {
  const stdout = captureWrites(process.stdout);
  const stderr = captureWrites(process.stderr);

  try {
    const exitCode = cli.runCli(["init", "deploy"], {}, () => {
      throw new Error("generic spawn should not run for init");
    }, {
      execute() {
        throw new Error("Invalid init option: deploy");
      },
    });

    assert.equal(exitCode, 1);
    assert.equal(stdout.output(), "");
    assert.equal(stderr.output(), "Invalid init option: deploy\n");
  } finally {
    stdout.restore();
    stderr.restore();
  }
});

test("runCli keeps the generic route for non-init commands", () => {
  const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), "catamesh-generic-"));
  const jarPath = path.join(tmpDir, "core.jar");
  fs.writeFileSync(jarPath, "");

  const invocations = [];
  const exitCode = cli.runCli(
    ["diff", "spec.yaml"],
    {CATAMESH_CORE_JAR: jarPath},
    (command, args, options) => {
      invocations.push({command, args: [...args], options});
      return {status: 7, signal: null};
    },
  );

  assert.equal(exitCode, 7);
  assert.equal(invocations.length, 1);
  assert.equal(invocations[0].command, process.platform === "win32" ? "java.exe" : "java");
  assert.deepEqual(invocations[0].args, ["-jar", jarPath, "diff", "spec.yaml"]);
  assert.equal(invocations[0].options.stdio, "inherit");
});
