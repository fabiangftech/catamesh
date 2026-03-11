import {DiffSummary} from "./DiffSummary";
import {DiffTreeNode} from "./DiffTreeNode";
import {PolicyRule} from "./PolicyRule";

export interface DiffResult {
    root: DiffTreeNode;
    summary: DiffSummary;
    policyRules?: PolicyRule[] | null;
}
