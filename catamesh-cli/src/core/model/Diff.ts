import {DiffSummary} from "./DiffSummary";
import {DiffSection} from "./DiffSection";

export interface Diff {
    dataProductName: string;
    summary: DiffSummary;
    sections: Array<DiffSection>;
}