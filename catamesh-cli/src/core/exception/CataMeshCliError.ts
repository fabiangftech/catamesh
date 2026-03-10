export class CataMeshCliError extends Error {

    private readonly _status: number | null;
    private readonly _errorCode: string;
    private readonly _title: string;
    private readonly _hint?: string;
    private readonly _details: string[];

    constructor(
        status: number | null,
        message: string,
        errorCode = "UNKNOWN_CLI_ERROR",
        title = "CLI command failed",
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
