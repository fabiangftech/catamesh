const test = require("node:test");
const assert = require("node:assert/strict");
const path = require("node:path");
const {spawnSync} = require("node:child_process");

const {runCli} = require("../dist/cli.js");
const {CataMeshCliError} = require("../dist/core/exception/CataMeshCliError.js");
const {stripAnsi} = require("./test-helpers.js");

test("runCli dispatches each supported command and preserves the original argv", () => {
    const cases = [
        ["new", "templateFacadeFactory"],
        ["plan", "planFacadeFactory"],
        ["apply", "applyFacadeFactory"],
        ["get", "getFacadeFactory"],
        ["diff", "diffFacadeFactory"],
    ];

    cases.forEach(([commandName, factoryKey]) => {
        const originalCommand = [commandName, "sample"];
        const calls = [];
        const dependencies = {
            [factoryKey]: () => ({
                run(receivedCommand) {
                    calls.push(receivedCommand);
                    receivedCommand[0] = `${commandName}-handled`;
                },
            }),
            writeError() {
                assert.fail(`writeError should not be called for ${commandName}`);
            },
        };

        const result = runCli(originalCommand, dependencies);

        assert.equal(result, 0);
        assert.equal(calls.length, 1);
        assert.notEqual(calls[0], originalCommand);
        assert.equal(calls[0][0], `${commandName}-handled`);
        assert.deepEqual(originalCommand, [commandName, "sample"]);
    });
});

test("runCli returns success for help, unknown commands and empty argv", () => {
    const errors = [];

    assert.equal(runCli(["help"], {writeError: (message) => errors.push(message)}), 0);
    assert.equal(runCli(["unknown"], {writeError: (message) => errors.push(message)}), 0);
    assert.equal(runCli([], {writeError: (message) => errors.push(message)}), 0);
    assert.deepEqual(errors, []);
});

test("runCli renders cli errors even when hint and details are absent", () => {
    const errors = [];

    const result = runCli(
        ["new", "data-product", "demo"],
        {
            templateFacadeFactory: () => ({
                run() {
                    throw new CataMeshCliError(
                        20,
                        "Template name demo is invalid.",
                        "VALIDATION_ERROR",
                        "Invalid template input",
                    );
                },
            }),
            writeError: (message) => errors.push(stripAnsi(message)),
        },
    );

    assert.equal(result, 20);
    assert.equal(errors.length, 1);
    assert.match(errors[0], /Command failed/);
    assert.match(errors[0], /Command: cata new data-product demo/);
    assert.match(errors[0], /Reason: Invalid template input/);
    assert.match(errors[0], /Message: Template name demo is invalid\./);
    assert.doesNotMatch(errors[0], /Hint:/);
    assert.doesNotMatch(errors[0], /Details:/);
});

test("runCli falls back to generic formatting for Error and non-Error failures", () => {
    [
        new Error("boom"),
        "boom",
    ].forEach((thrown) => {
        const errors = [];

        const result = runCli(
            ["new", "data-product", "demo"],
            {
                templateFacadeFactory: () => ({
                    run() {
                        throw thrown;
                    },
                }),
                writeError: (message) => errors.push(message),
            },
        );

        assert.equal(result, 1);
        assert.equal(errors.length, 1);
        assert.equal(errors[0], "Error: boom");
    });
});

test("cli entrypoint exits successfully when executed as a script", () => {
    const cliPath = path.resolve(__dirname, "../dist/cli.js");
    const result = spawnSync(process.execPath, [cliPath, "help"], {encoding: "utf8"});

    assert.equal(result.status, 0);
    assert.equal(result.stdout, "");
    assert.equal(result.stderr, "");
});
