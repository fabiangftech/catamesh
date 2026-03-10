import {CoreErrorPayload} from "../model/CoreErrorPayload";

export class CataMeshCoreError extends Error {

    private _status: number | null;
    private _errorCode: string;
    private _title: string;
    private _hint?: string;
    private _details: string[];

    constructor(
        status: number | null,
        message: string,
        errorCode = "UNKNOWN_CORE_ERROR",
        title = "Core command failed",
        hint?: string,
        details: string[] = [],
    ) {
        super(message);
        this._status = status;
        this._errorCode = errorCode;
        this._title = title;
        this._hint = hint;
        this._details = details;
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

    get status(): number | null {
        return this._status;
    }

    get errorCode(): string {
        return this._errorCode;
    }

    get title(): string {
        return this._title;
    }

    get hint(): string | undefined {
        return this._hint;
    }

    get details(): string[] {
        return this._details;
    }
}
