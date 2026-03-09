import {Command} from "../../core/cqrs/Command";
import {Diff} from "../../core/model/Diff";

export class DiffPrintCommand implements Command<Diff, void>{
    execute(diff: Diff): void {
        return undefined;
    }
}