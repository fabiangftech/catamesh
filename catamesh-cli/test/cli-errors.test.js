const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const os = require("node:os");
const path = require("node:path");

const {runCli} = require("../dist/cli.js");
const {CataMeshCoreError} = require("../dist/core/exception/CataMeshCoreError.js");

const ANSI_PATTERN = /\x1B\[[0-9;]*m/g;

function stripAnsi(input) {
    return input.replace(ANSI_PATTERN, "");
}

function withCapturedLogs(fn) {
    const originalLog = console.log;
    const messages = [];
    console.log = (...args) => messages.push(args.join(" "));

    try {
        const result = fn();
        return {result, messages};
    } finally {
        console.log = originalLog;
    }
}

function createTempYaml(contents) {
    const dir = fs.mkdtempSync(path.join(os.tmpdir(), "catamesh-cli-"));
    const filePath = path.join(dir, "invalid-schema.yaml");
    fs.writeFileSync(filePath, contents, "utf8");
    return filePath;
}

test("runCli standardizes apply invalid schema errors", () => {
    const filePath = createTempYaml("schemaVersion: deploy/v1\n");
    const errors = [];

    const {result, messages} = withCapturedLogs(() => runCli(
        ["apply", filePath],
        {writeError: (message) => errors.push(stripAnsi(message))},
    ));

    assert.equal(result, 20);
    assert.deepEqual(messages, []);
    assert.equal(errors.length, 1);
    assert.match(errors[0], /Command failed/);
    assert.match(errors[0], /Code: VALIDATION_ERROR \(20\)/);
    assert.match(errors[0], /Command: cata apply/);
    assert.match(errors[0], /Reason: Invalid schema version/);
    assert.match(errors[0], new RegExp(`Message: File ${filePath.replace(/[.*+?^${}()|[\]\\]/g, "\\$&")} is not supported by cata apply\\.`));
    assert.match(errors[0], /Hint: Use a YAML with schemaVersion: data-product\/v1\./);
    assert.match(errors[0], /supported schemaVersion: data-product\/v1/);
});

test("runCli standardizes plan invalid schema errors without leaking file name logs", () => {
    const filePath = createTempYaml("schemaVersion: env/v1\n");
    const errors = [];

    const {result, messages} = withCapturedLogs(() => runCli(
        ["plan", filePath],
        {writeError: (message) => errors.push(stripAnsi(message))},
    ));

    assert.equal(result, 20);
    assert.deepEqual(messages, []);
    assert.equal(errors.length, 1);
    assert.match(errors[0], /Command: cata plan/);
    assert.match(errors[0], /Reason: Invalid schema version/);
});

test("runCli standardizes diff invalid schema errors without leaking file name logs", () => {
    const filePath = createTempYaml("schemaVersion: something-else/v1\n");
    const errors = [];

    const {result, messages} = withCapturedLogs(() => runCli(
        ["diff", filePath],
        {writeError: (message) => errors.push(stripAnsi(message))},
    ));

    assert.equal(result, 20);
    assert.deepEqual(messages, []);
    assert.equal(errors.length, 1);
    assert.match(errors[0], /Command: cata diff/);
    assert.match(errors[0], /Reason: Invalid schema version/);
});

test("runCli standardizes get invalid type errors", () => {
    const errors = [];

    const {result, messages} = withCapturedLogs(() => runCli(
        ["get", "invalid-type", "demo"],
        {writeError: (message) => errors.push(stripAnsi(message))},
    ));

    assert.equal(result, 20);
    assert.deepEqual(messages, []);
    assert.equal(errors.length, 1);
    assert.match(errors[0], /Command: cata get invalid-type demo/);
    assert.match(errors[0], /Reason: Invalid model type/);
    assert.match(errors[0], /Message: Type invalid-type is not supported by cata get\./);
    assert.match(errors[0], /Hint: Use data-product\./);
    assert.match(errors[0], /supported types: data-product/);
});

test("runCli keeps rendering core errors with the shared standard format", () => {
    const errors = [];

    const {result, messages} = withCapturedLogs(() => runCli(
        ["apply", "demo"],
        {
            applyFacadeFactory: () => ({
                run() {
                    throw new CataMeshCoreError(
                        21,
                        "Resource definition component-1 version 0.0.3 already exists and cannot be changed in place.",
                        "CONFLICT_ERROR",
                        "Definition version is immutable",
                        "Bump definition.version to a new value, for example 0.0.4, and run cata apply again.",
                        ["config"],
                    );
                },
            }),
            writeError: (message) => errors.push(stripAnsi(message)),
        },
    ));

    assert.equal(result, 21);
    assert.deepEqual(messages, []);
    assert.equal(errors.length, 1);
    assert.match(errors[0], /Code: CONFLICT_ERROR \(21\)/);
    assert.match(errors[0], /Reason: Definition version is immutable/);
    assert.match(errors[0], /Hint: Bump definition\.version/);
});

test("runCli renders validation errors without leaking raw core stderr noise", () => {
    const errors = [];

    const {result, messages} = withCapturedLogs(() => runCli(
        ["diff", "test--my-first-data-product"],
        {
            diffFacadeFactory: () => ({
                run() {
                    throw new CataMeshCoreError(
                        20,
                        "The provided YAML does not match the expected schema.",
                        "VALIDATION_ERROR",
                        "Schema validation failed",
                        undefined,
                        ["name=required property 'name' not found"],
                    );
                },
            }),
            writeError: (message) => errors.push(stripAnsi(message)),
        },
    ));

    assert.equal(result, 20);
    assert.deepEqual(messages, []);
    assert.equal(errors.length, 1);
    assert.match(errors[0], /Command: cata diff test--my-first-data-product/);
    assert.match(errors[0], /Reason: Schema validation failed/);
    assert.match(errors[0], /Message: The provided YAML does not match the expected schema\./);
    assert.match(errors[0], /Details:\n- name: required property 'name' not found/);
    assert.doesNotMatch(errors[0], /\{"errorCode"/);
    assert.doesNotMatch(errors[0], /SLF4J/);
});
