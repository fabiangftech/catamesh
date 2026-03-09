export class CataMeshCoreError extends Error {

    private _status: number | null;

    constructor(status: number, message: string) {
        super(message);
        this._status = status;
    }

    get status(): number | null {
        return this._status;
    }
}