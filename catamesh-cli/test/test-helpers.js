const fs = require("node:fs");
const os = require("node:os");
const path = require("node:path");

const ANSI_PATTERN = /\x1B\[[0-9;]*m/g;

function stripAnsi(input) {
    return input.replace(ANSI_PATTERN, "");
}

function captureConsole(fn) {
    const originalLog = console.log;
    const originalError = console.error;
    const logs = [];
    const errors = [];

    console.log = (...args) => logs.push(args.join(" "));
    console.error = (...args) => errors.push(args.join(" "));

    try {
        const result = fn();
        return {result, logs, errors};
    } finally {
        console.log = originalLog;
        console.error = originalError;
    }
}

function createTempDir(prefix = "catamesh-cli-") {
    return fs.mkdtempSync(path.join(os.tmpdir(), prefix));
}

function createYamlFile(contents, fileName = "data-product.yaml") {
    const dir = createTempDir();
    const filePath = path.join(dir, fileName);
    fs.writeFileSync(filePath, contents, "utf8");

    return {
        dir,
        filePath,
        basePath: filePath.replace(/\.(yaml|yml)$/u, ""),
    };
}

function createResource(overrides = {}) {
    return {
        dataProductId: "dp-analytics",
        definition: {
            config: {format: "parquet", location: "s3://lake/orders"},
            schemaVersion: "resource-definition/v1",
            version: "1.0.0",
        },
        displayName: "Orders Table",
        id: "resource-orders",
        kind: "table",
        name: "orders",
        ...overrides,
    };
}

function createDataProduct(overrides = {}) {
    const resources = overrides.resources ?? [createResource()];

    return {
        schemaVersion: "data-product/v1",
        metadata: {
            description: "Curated commerce analytics",
            displayName: "Analytics Product",
            domain: "analytics",
            id: "dp-analytics",
            name: "analytics-product",
        },
        spec: {
            kind: "batch",
            resources,
        },
        ...overrides,
    };
}

function createPlan(overrides = {}) {
    return {
        action: "create",
        dataProductName: "analytics-product",
        requestId: "req-123",
        resources: [
            {
                action: "create",
                name: "orders",
                type: "resource",
                version: "1.0.0",
            },
        ],
        summary: {
            adopt: 0,
            create: 1,
            delete: 0,
            noop: 0,
            replace: 0,
            update: 0,
        },
        ...overrides,
    };
}

function createApplyResult(overrides = {}) {
    return {
        dataProduct: createDataProduct(),
        plan: createPlan(),
        ...overrides,
    };
}

function createDiffNode(overrides = {}) {
    return {
        changeType: "NONE",
        elements: [],
        entries: {},
        fields: {},
        kind: "MAP",
        newValue: null,
        oldValue: null,
        path: "",
        ...overrides,
    };
}

function createPolicyRule(overrides = {}) {
    return {
        level: "error",
        message: "Definition version is immutable.",
        path: "spec.resources.orders.definition.version",
        ...overrides,
    };
}

function createDiffResult(overrides = {}) {
    return {
        policyRules: null,
        root: createDiffNode(),
        summary: {
            added: 0,
            changed: 0,
            removed: 0,
        },
        ...overrides,
    };
}

module.exports = {
    captureConsole,
    createApplyResult,
    createDataProduct,
    createDiffNode,
    createDiffResult,
    createPolicyRule,
    createResource,
    createPlan,
    createTempDir,
    createYamlFile,
    stripAnsi,
};
