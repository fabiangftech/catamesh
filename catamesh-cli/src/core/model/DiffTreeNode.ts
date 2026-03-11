import {DiffChangeType} from "./DiffChangeType";
import {DiffNodeKind} from "./DiffNodeKind";

export interface DiffTreeNode {
    path: string;
    kind: DiffNodeKind;
    changeType: DiffChangeType;
    oldValue: unknown;
    newValue: unknown;
    fields: Record<string, DiffTreeNode>;
    elements: Array<DiffTreeNode>;
    entries: Record<string, DiffTreeNode>;
}
