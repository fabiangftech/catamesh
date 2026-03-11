import {DiffSummary} from "./DiffSummary";
import {DiffTreeNode} from "./DiffTreeNode";

export interface DiffResult {
    root: DiffTreeNode;
    summary: DiffSummary;
}
