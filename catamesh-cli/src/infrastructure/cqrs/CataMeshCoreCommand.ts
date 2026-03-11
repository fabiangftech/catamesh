import {
    spawnSync,
} from "node:child_process";
import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreConfig, SpawnProcess} from "../config/CataMeshCoreConfig";
import {CataMeshCoreError} from "../../core/exception/CataMeshCoreError";
import {CoreErrorPayload} from "../../core/model/CoreErrorPayload";

export class CataMeshCoreCommand implements Query<string[], string> {
    constructor(private readonly spawnProcess: SpawnProcess = spawnSync) {
    }

    execute(args: string[]): string {
        const child = this.spawnProcess(
            "java",
            ["-jar", CataMeshCoreConfig.CORE_JAR_PATH, args[0], ...args.slice(1)],
            {encoding: "utf8"},
        );
        if (child.error) {
            throw child.error;
        }
        if (child.status !== 0) {
            throw this.parseCoreError(child.status, child.stderr);
        }
        return child.stdout;
    }

    private parseCoreError(status: number | null, stderr: string): CataMeshCoreError {
        const parsedPayload = this.tryParsePayload(stderr)
            ?? this.tryParsePayload(this.lastNonEmptyLine(stderr));
        if (parsedPayload) {
            return CataMeshCoreError.fromPayload(parsedPayload);
        }

        return CataMeshCoreError.fromRaw(status, stderr);
    }

    private tryParsePayload(payload: string): CoreErrorPayload | null {
        const normalizedPayload = payload.trim();
        if (!normalizedPayload) {
            return null;
        }

        try {
            const parsed = JSON.parse(normalizedPayload) as Partial<CoreErrorPayload>;
            if (
                typeof parsed.errorCode === "string"
                && typeof parsed.status === "number"
                && typeof parsed.title === "string"
                && typeof parsed.message === "string"
            ) {
                return {
                    errorCode: parsed.errorCode,
                    status: parsed.status,
                    title: parsed.title,
                    message: parsed.message,
                    hint: typeof parsed.hint === "string" ? parsed.hint : undefined,
                    details: Array.isArray(parsed.details)
                        ? parsed.details.filter((detail): detail is string => typeof detail === "string")
                        : undefined,
                };
            }
        } catch (_error) {
            // Fallback to the raw stderr when the core does not emit structured JSON.
        }

        return null;
    }

    private lastNonEmptyLine(stderr: string): string {
        const lines = stderr
            .split(/\r?\n/)
            .map((line) => line.trim())
            .filter((line) => line.length > 0);
        return lines.at(-1) ?? "";
    }
}
