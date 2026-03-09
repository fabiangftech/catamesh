import {DiffOp} from "./DiffOp";

export interface DiffChange {
    op: DiffOp;
    path: string;
    current: unknown;
    desired: unknown;

}