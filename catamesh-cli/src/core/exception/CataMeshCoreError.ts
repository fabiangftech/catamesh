import {CataMeshCliError} from "./CataMeshCliError";
import {CoreErrorPayload} from "../model/CoreErrorPayload";

export class CataMeshCoreError extends CataMeshCliError {

    constructor(
        status: number | null,
        message: string,
        errorCode = "UNKNOWN_CORE_ERROR",
        title = "Core command failed",
        hint?: string,
        details: string[] = [],
    ) {
        super(status, message, errorCode, title, hint, details);
    }

    static fromPayload(payload: CoreErrorPayload): CataMeshCoreError {
        return new CataMeshCoreError(
            payload.status,
            payload.message,
            payload.errorCode,
            payload.title,
            payload.hint,
            payload.details ?? [],
        );
    }

    static fromRaw(status: number | null, rawMessage: string): CataMeshCoreError {
        const message = rawMessage.trim() || "Core command failed";
        return new CataMeshCoreError(status, message);
    }
}
