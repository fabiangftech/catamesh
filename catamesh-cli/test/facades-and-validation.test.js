const test = require("node:test");
const assert = require("node:assert/strict");
const fs = require("node:fs");
const path = require("node:path");

const {ApplyFacade} = require("../dist/application/facade/ApplyFacade.js");
const {assertDataProductSchema, resolveYamlFileName} = require("../dist/application/facade/DataProductYamlSupport.js");
const {DiffFacade} = require("../dist/application/facade/DiffFacade.js");
const {GetFacade} = require("../dist/application/facade/GetFacade.js");
const {PlanFacade} = require("../dist/application/facade/PlanFacade.js");
const {TemplateFacade} = require("../dist/application/facade/TemplateFacade.js");
const {CataMeshCliError} = require("../dist/core/exception/CataMeshCliError.js");
const {Schema} = require("../dist/core/model/Schema.js");
const {
    createApplyResult,
    createDataProduct,
    createDiffNode,
    createDiffResult,
    createPlan,
    createTempDir,
    createYamlFile,
} = require("./test-helpers.js");

test("resolveYamlFileName preserves yaml extensions and appends the default one", () => {
    assert.equal(resolveYamlFileName("demo"), "demo.yaml");
    assert.equal(resolveYamlFileName("demo.yaml"), "demo.yaml");
    assert.equal(resolveYamlFileName("demo.yml"), "demo.yml");
});

test("assertDataProductSchema accepts data product yaml and rejects unsupported schema versions", () => {
    assert.doesNotThrow(() => assertDataProductSchema("apply", "demo.yaml", `schemaVersion: ${Schema.data_product_v1}\n`));

    let error;
    try {
        assertDataProductSchema("plan", "invalid.yaml", "schemaVersion: deploy/v1\n");
    } catch (cause) {
        error = cause;
    }

    assert.ok(error instanceof CataMeshCliError);
    assert.equal(error.status, 20);
    assert.equal(error.errorCode, "VALIDATION_ERROR");
    assert.equal(error.title, "Invalid schema version");
    assert.match(error.message, /File invalid\.yaml is not supported by cata plan\./);
});

test("ApplyFacade reads yaml, replaces the command payload and prints the parsed result", () => {
    const {basePath} = createYamlFile("schemaVersion: data-product/v1\nmetadata: {}\n");
    const facade = new ApplyFacade();
    const command = ["apply", basePath];
    const applyResult = createApplyResult();
    let executedCommand;
    let printedResult;

    facade.cataMeshCoreCommand = {
        execute(receivedCommand) {
            executedCommand = [...receivedCommand];
            return JSON.stringify(applyResult);
        },
    };
    facade.applyPrintCommand = {
        execute(receivedResult) {
            printedResult = receivedResult;
        },
    };

    const result = facade.run(command);

    assert.equal(result, undefined);
    assert.equal(command[1], "schemaVersion: data-product/v1\nmetadata: {}\n");
    assert.deepEqual(executedCommand, ["apply", "schemaVersion: data-product/v1\nmetadata: {}\n"]);
    assert.deepEqual(printedResult, applyResult);
});

test("PlanFacade reads yaml and delegates the parsed plan to its print command", () => {
    const {filePath} = createYamlFile("schemaVersion: data-product/v1\nkind: batch\n", "plan-input.yml");
    const facade = new PlanFacade();
    const command = ["plan", filePath];
    const plan = createPlan({
        requestId: "req-plan",
        summary: {
            adopt: 0,
            create: 2,
            delete: 0,
            noop: 1,
            replace: 0,
            update: 1,
        },
    });
    let executedCommand;
    let printedPlan;

    facade.cataMeshCoreCommand = {
        execute(receivedCommand) {
            executedCommand = [...receivedCommand];
            return JSON.stringify(plan);
        },
    };
    facade.planPrintCommand = {
        execute(receivedPlan) {
            printedPlan = receivedPlan;
        },
    };

    facade.run(command);

    assert.equal(command[1], "schemaVersion: data-product/v1\nkind: batch\n");
    assert.deepEqual(executedCommand, ["plan", "schemaVersion: data-product/v1\nkind: batch\n"]);
    assert.deepEqual(printedPlan, plan);
});

test("DiffFacade reads yaml and delegates the parsed diff to its print command", () => {
    const {filePath} = createYamlFile("schemaVersion: data-product/v1\nname: analytics\n", "diff-input.yaml");
    const facade = new DiffFacade();
    const command = ["diff", filePath];
    const diff = createDiffResult({
        root: createDiffNode({
            entries: {
                metadata: createDiffNode({
                    entries: {
                        name: createDiffNode({
                            changeType: "CREATE",
                            kind: "VALUE",
                            newValue: "analytics",
                            path: "metadata.name",
                        }),
                    },
                    path: "metadata",
                }),
            },
        }),
        summary: {
            added: 1,
            changed: 0,
            removed: 0,
        },
    });
    let executedCommand;
    let printedDiff;

    facade.cataMeshCoreCommand = {
        execute(receivedCommand) {
            executedCommand = [...receivedCommand];
            return JSON.stringify(diff);
        },
    };
    facade.diffPrintCommand = {
        execute(receivedDiff) {
            printedDiff = receivedDiff;
        },
    };

    facade.run(command);

    assert.equal(command[1], "schemaVersion: data-product/v1\nname: analytics\n");
    assert.deepEqual(executedCommand, ["diff", "schemaVersion: data-product/v1\nname: analytics\n"]);
    assert.deepEqual(printedDiff, diff);
});

test("GetFacade fetches supported model types and prints the parsed data product", () => {
    const facade = new GetFacade();
    const command = ["get", "data-product", "analytics-product"];
    const dataProduct = createDataProduct();
    let executedCommand;
    let printedDataProduct;

    facade.cataMeshCoreCommand = {
        execute(receivedCommand) {
            executedCommand = [...receivedCommand];
            return JSON.stringify(dataProduct);
        },
    };
    facade.dataProductPrintCommand = {
        execute(receivedDataProduct) {
            printedDataProduct = receivedDataProduct;
        },
    };

    const result = facade.run(command);

    assert.equal(result, undefined);
    assert.deepEqual(executedCommand, command);
    assert.deepEqual(printedDataProduct, dataProduct);
});

test("GetFacade rejects unsupported model types", () => {
    const facade = new GetFacade();

    assert.throws(
        () => facade.run(["get", "invalid-type", "analytics-product"]),
        (error) => {
            assert.ok(error instanceof CataMeshCliError);
            assert.equal(error.status, 20);
            assert.equal(error.title, "Invalid model type");
            assert.match(error.message, /Type invalid-type is not supported by cata get\./);
            return true;
        },
    );
});

test("TemplateFacade writes the yaml returned by the core command to disk", () => {
    const facade = new TemplateFacade();
    const dir = createTempDir();
    const target = path.join(dir, "generated-template");
    const command = ["new", "data-product", target];
    let executedCommand;

    facade.cataMeshCoreCommand = {
        execute(receivedCommand) {
            executedCommand = [...receivedCommand];
            return "schemaVersion: data-product/v1\nmetadata:\n  name: generated-template\n";
        },
    };

    const result = facade.run(command);

    assert.equal(result, undefined);
    assert.deepEqual(executedCommand, command);
    assert.equal(
        fs.readFileSync(`${target}.yaml`, "utf8"),
        "schemaVersion: data-product/v1\nmetadata:\n  name: generated-template\n",
    );
});
