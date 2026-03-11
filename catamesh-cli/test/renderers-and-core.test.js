const test = require("node:test");
const assert = require("node:assert/strict");

const {ApplyPrintCommand} = require("../dist/infrastructure/cqrs/ApplyPrintCommand.js");
const {CataMeshCoreCommand} = require("../dist/infrastructure/cqrs/CataMeshCoreCommand.js");
const {CataMeshCoreConfig} = require("../dist/infrastructure/config/CataMeshCoreConfig.js");
const {CataMeshCoreError} = require("../dist/core/exception/CataMeshCoreError.js");
const {DataProductPrintCommand} = require("../dist/infrastructure/cqrs/DataProductPrintCommand.js");
const {DiffPrintCommand} = require("../dist/infrastructure/cqrs/DiffPrintCommand.js");
const {PlanPrintCommand} = require("../dist/infrastructure/cqrs/PlanPrintCommand.js");
const {
    captureConsole,
    createApplyResult,
    createDataProduct,
    createDiffNode,
    createDiffResult,
    createPlan,
    createResource,
    stripAnsi,
} = require("./test-helpers.js");

test("CataMeshCoreCommand executes the embedded jar and returns stdout", () => {
    const calls = [];
    const command = new CataMeshCoreCommand((binary, args, options) => {
        calls.push({args, binary, options});
        return {
            error: undefined,
            status: 0,
            stderr: "",
            stdout: "{\"ok\":true}",
        };
    });

    const output = command.execute(["plan", "payload"]);

    assert.equal(output, "{\"ok\":true}");
    assert.equal(calls.length, 1);
    assert.equal(calls[0].binary, "java");
    assert.deepEqual(
        calls[0].args,
        ["-jar", CataMeshCoreConfig.CORE_JAR_PATH, "plan", "payload"],
    );
    assert.equal(calls[0].options.encoding, "utf8");
});

test("CataMeshCoreCommand rethrows child process errors", () => {
    const failure = new Error("spawn failed");
    const command = new CataMeshCoreCommand(() => ({
        error: failure,
        status: null,
        stderr: "",
        stdout: "",
    }));

    assert.throws(
        () => command.execute(["get", "data-product", "demo"]),
        (error) => error === failure,
    );
});

test("CataMeshCoreCommand parses structured stderr payloads", () => {
    const command = new CataMeshCoreCommand(() => ({
        error: undefined,
        status: 21,
        stderr: JSON.stringify({
            details: ["config", 10, "schemaVersion"],
            errorCode: "CONFLICT_ERROR",
            hint: "Bump definition.version and retry.",
            message: "Definition version is immutable.",
            status: 21,
            title: "Immutable definition",
        }),
        stdout: "",
    }));

    assert.throws(
        () => command.execute(["apply", "payload"]),
        (error) => {
            assert.ok(error instanceof CataMeshCoreError);
            assert.equal(error.status, 21);
            assert.equal(error.errorCode, "CONFLICT_ERROR");
            assert.equal(error.title, "Immutable definition");
            assert.equal(error.hint, "Bump definition.version and retry.");
            assert.deepEqual(error.details, ["config", "schemaVersion"]);
            return true;
        },
    );
});

test("CataMeshCoreCommand falls back to raw stderr for invalid and incomplete payloads", () => {
    const invalidJsonCommand = new CataMeshCoreCommand(() => ({
        error: undefined,
        status: 1,
        stderr: "plain stderr message\n",
        stdout: "",
    }));
    const incompletePayloadCommand = new CataMeshCoreCommand(() => ({
        error: undefined,
        status: 2,
        stderr: JSON.stringify({status: "bad-shape"}),
        stdout: "",
    }));

    assert.throws(
        () => invalidJsonCommand.execute(["diff", "payload"]),
        (error) => {
            assert.ok(error instanceof CataMeshCoreError);
            assert.equal(error.status, 1);
            assert.equal(error.errorCode, "UNKNOWN_CORE_ERROR");
            assert.equal(error.title, "Core command failed");
            assert.equal(error.message, "plain stderr message");
            return true;
        },
    );

    assert.throws(
        () => incompletePayloadCommand.execute(["diff", "payload"]),
        (error) => {
            assert.ok(error instanceof CataMeshCoreError);
            assert.equal(error.status, 2);
            assert.equal(error.message, "{\"status\":\"bad-shape\"}");
            return true;
        },
    );
});

test("DataProductPrintCommand renders empty resource lists", () => {
    const command = new DataProductPrintCommand();
    const {logs} = captureConsole(() => command.execute(createDataProduct({spec: {kind: "batch", resources: []}})));
    const rendered = stripAnsi(logs.join("\n"));

    assert.match(rendered, /ID: dp-analytics/);
    assert.match(rendered, /Resources:/);
    assert.match(rendered, /\(none\)/);
});

test("DataProductPrintCommand renders populated resource lists", () => {
    const command = new DataProductPrintCommand();
    const resourceA = createResource();
    const resourceB = createResource({
        dataProductId: "dp-analytics",
        displayName: "Payments Stream",
        id: "resource-payments",
        kind: "stream",
        name: "payments",
    });
    const {logs} = captureConsole(() => command.execute(createDataProduct({spec: {kind: "batch", resources: [resourceA, resourceB]}})));
    const rendered = stripAnsi(logs.join("\n"));

    assert.match(rendered, /- orders/);
    assert.match(rendered, /Definition:/);
    assert.match(rendered, /Version: 1\.0\.0/);
    assert.match(rendered, /Config: \{"format":"parquet","location":"s3:\/\/lake\/orders"\}/);
    assert.match(rendered, /- payments/);
});

test("ApplyPrintCommand renders the final state summary and delegates the data product body", () => {
    const command = new ApplyPrintCommand();
    const applyResult = createApplyResult();
    let printedDataProduct;

    command.dataProductPrintCommand = {
        execute(dataProduct) {
            printedDataProduct = dataProduct;
        },
    };

    const {result, logs} = captureConsole(() => command.execute(applyResult));
    const rendered = stripAnsi(logs.join("\n"));

    assert.equal(result, undefined);
    assert.deepEqual(printedDataProduct, applyResult.dataProduct);
    assert.match(rendered, /Final State:/);
    assert.match(rendered, /Data Product: analytics-product/);
    assert.match(rendered, /Request ID: req-123/);
    assert.match(rendered, /Apply: 1 to create, 0 to update, 0 to delete, 0 to noop\./);
});

test("PlanPrintCommand renders resources with and without versions", () => {
    const command = new PlanPrintCommand();
    const plan = createPlan({
        resources: [
            {action: "create", name: "orders", type: "resource", version: "1.0.0"},
            {action: "update", name: "payments", type: "resource-definition", version: ""},
        ],
        summary: {
            adopt: 0,
            create: 1,
            delete: 1,
            noop: 0,
            replace: 0,
            update: 1,
        },
    });
    const {logs} = captureConsole(() => command.execute(plan));
    const rendered = stripAnsi(logs.join("\n"));

    assert.match(rendered, /Plan: 1 to create, 1 to update, 1 to delete, 0 to noop\./);
    assert.match(rendered, /\+ resource\.orders \(v1\.0\.0\)/);
    assert.match(rendered, /\+ resource-definition\.payments/);
});

test("DiffPrintCommand renders the empty diff state", () => {
    const command = new DiffPrintCommand();
    const {logs} = captureConsole(() => command.execute(createDiffResult()));
    const rendered = stripAnsi(logs.join("\n"));

    assert.match(rendered, /Diff: 0 added, 0 changed, 0 removed\./);
    assert.match(rendered, /Changes:/);
    assert.match(rendered, /\(no changes\)/);
});

test("DiffPrintCommand renders create, delete and update changes from the v2 tree", () => {
    const command = new DiffPrintCommand();
    const diff = createDiffResult({
        root: createDiffNode({
            entries: {
                metadata: createDiffNode({
                    entries: {
                        name: createDiffNode({
                            changeType: "CREATE",
                            kind: "VALUE",
                            newValue: "analytics-product",
                            path: "metadata.name",
                        }),
                        owner: createDiffNode({
                            changeType: "CREATE",
                            kind: "VALUE",
                            newValue: "platform",
                            path: "metadata.owner",
                        }),
                        description: createDiffNode({
                            changeType: "DELETE",
                            kind: "VALUE",
                            oldValue: "Legacy description",
                            path: "metadata.description",
                        }),
                        domain: createDiffNode({
                            changeType: "NONE",
                            kind: "VALUE",
                            newValue: "analytics",
                            oldValue: "analytics",
                            path: "metadata.domain",
                        }),
                    },
                    path: "metadata",
                }),
                spec: createDiffNode({
                    entries: {
                        resources: createDiffNode({
                            entries: {
                                orders: createDiffNode({
                                    entries: {
                                        definition: createDiffNode({
                                            entries: {
                                                config: createDiffNode({
                                                    entries: {
                                                        format: createDiffNode({
                                                            changeType: "UPDATE",
                                                            kind: "VALUE",
                                                            newValue: "parquet",
                                                            oldValue: "json",
                                                            path: "spec.resources.orders.definition.config.format",
                                                        }),
                                                    },
                                                    path: "spec.resources.orders.definition.config",
                                                }),
                                            },
                                            path: "spec.resources.orders.definition",
                                        }),
                                    },
                                    path: "spec.resources.orders",
                                }),
                            },
                            path: "spec.resources",
                        }),
                    },
                    path: "spec",
                }),
            },
        }),
        summary: {
            added: 2,
            changed: 1,
            removed: 1,
        },
    });
    const {logs} = captureConsole(() => command.execute(diff));
    const rendered = stripAnsi(logs.join("\n"));

    assert.match(rendered, /Diff: 2 added, 1 changed, 1 removed\./);
    assert.match(rendered, /Changes:/);
    assert.match(rendered, /\+ metadata\.name: analytics-product/);
    assert.match(rendered, /\+ metadata\.owner: platform/);
    assert.match(rendered, /- metadata\.description: Legacy description/);
    assert.match(rendered, /~ spec\.resources\.orders\.definition\.config\.format/);
    assert.match(rendered, /Current: json/);
    assert.match(rendered, /Desired: parquet/);
    assert.doesNotMatch(rendered, /metadata\.domain/);
});
