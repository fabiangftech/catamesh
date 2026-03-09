const test = require("node:test");
const assert = require("node:assert/strict");
const path = require("node:path");

const {runCli} = require("../dist/cli.js");
const {InitQuery} = require("../dist/infrastructure/cqrs/InitQuery.js");

test("InitQuery invokes embedded core jar with new command", () => {
  const invocations = [];
  const query = new InitQuery((command, args, options) => {
    invocations.push({command, args: [...args], options});
    return {
      status: 0,
      signal: null,
      stdout: "schemaVersion: data-product/v1\n",
      stderr: "",
    };
  });

  const output = query.execute(["data-product", "mesh"]);

  assert.equal(output, "schemaVersion: data-product/v1\n");
  assert.equal(invocations.length, 1);
  assert.equal(invocations[0].command, "java");
  assert.deepEqual(
    invocations[0].args,
    [
      "-jar",
      path.resolve(__dirname, "..", "dist", "core", "catamesh-core-cli.jar"),
      "new",
      "data-product",
      "mesh",
    ],
  );
  assert.deepEqual(invocations[0].options, {encoding: "utf8"});
});

test("runCli writes Java failures to stderr and returns exit code 1", () => {
  let stdout = "";
  let stderr = "";

  const exitCode = runCli(
    ["new", "data-product", "mesh"],
    {
      execute() {
        throw new Error("Missing core jar");
      },
    },
    {
      write(chunk) {
        stdout += String(chunk);
      },
    },
    {
      write(chunk) {
        stderr += String(chunk);
      },
    },
  );

  assert.equal(exitCode, 1);
  assert.equal(stdout, "");
  assert.equal(stderr, "Missing core jar\n");
});
