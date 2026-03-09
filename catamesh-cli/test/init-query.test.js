const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const os = require("node:os");
const path = require("node:path");

const {InitQuery} = require("../dist/infrastructure/cqrs/InitQuery.js");

const createJarFile = () => {
  const tmpDir = fs.mkdtempSync(path.join(os.tmpdir(), "catamesh-init-query-"));
  const jarPath = path.join(tmpDir, "catamesh-core-cli.jar");
  fs.writeFileSync(jarPath, "");
  return jarPath;
};

test("InitQuery returns YAML from init data-product", () => {
  const jarPath = createJarFile();
  const invocations = [];
  const query = new InitQuery(
    {CATAMESH_CORE_JAR: jarPath},
    (command, args, options) => {
      invocations.push({command, args: [...args], options});
      return {
        status: 0,
        signal: null,
        stdout: "schemaVersion: data-product/v1\n",
        stderr: "",
      };
    },
  );

  const output = query.execute(["data-product", "mesh"]);

  assert.equal(output, "schemaVersion: data-product/v1\n");
  assert.equal(invocations.length, 1);
  assert.equal(invocations[0].command, process.platform === "win32" ? "java.exe" : "java");
  assert.deepEqual(invocations[0].args, ["-jar", jarPath, "init", "data-product", "mesh"]);
  assert.equal(invocations[0].options.encoding, "utf8");
});

test("InitQuery throws when the jar is missing", () => {
  const missingJar = path.join(os.tmpdir(), "missing-catamesh-core-cli.jar");
  const query = new InitQuery({CATAMESH_CORE_JAR: missingJar});

  assert.throws(
    () => query.execute(["data-product", "mesh"]),
    /CataMesh core JAR not found/,
  );
});

test("InitQuery throws when Java cannot start", () => {
  const jarPath = createJarFile();
  const query = new InitQuery(
    {CATAMESH_CORE_JAR: jarPath},
    () => ({
      status: null,
      signal: null,
      stdout: "",
      stderr: "",
      error: new Error("spawn java ENOENT"),
    }),
  );

  assert.throws(
    () => query.execute(["data-product", "mesh"]),
    /Failed to start Java runtime: spawn java ENOENT/,
  );
});

test("InitQuery throws stderr when core returns non-zero", () => {
  const jarPath = createJarFile();
  const query = new InitQuery(
    {CATAMESH_CORE_JAR: jarPath},
    () => ({
      status: 1,
      signal: null,
      stdout: "",
      stderr: "Invalid init option: deploy\nUsage: cata init data-product <name>\n",
    }),
  );

  assert.throws(
    () => query.execute(["deploy"]),
    /Invalid init option: deploy/,
  );
});
