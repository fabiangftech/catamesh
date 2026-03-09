import {DiffScope} from "./DiffScope";
import {DiffChange} from "./DiffChange";

export interface DiffSection {
    scope: DiffScope;
    name: string;
    changes: Array<DiffChange>
}