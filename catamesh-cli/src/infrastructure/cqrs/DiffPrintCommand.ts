import {Command} from "../../core/cqrs/Command";
import {Diff} from "../../core/model/Diff";
import {ColorConfig} from "../config/ColorConfig";
import {DiffScope} from "../../core/model/DiffScope";
import {DiffChange} from "../../core/model/DiffChange";
import {DiffOp} from "../../core/model/DiffOp";

export class DiffPrintCommand implements Command<Diff, void>{
    execute(diff: Diff): void {
        console.log(
            `${ColorConfig.cyan}Diff:${ColorConfig.reset} ` +
            `${ColorConfig.white}${diff.summary.add} to add, ${diff.summary.remove} to remove, ${diff.summary.replace} to replace.${ColorConfig.reset}`
        );
        console.log(`${ColorConfig.cyan}Data Product:${ColorConfig.reset} ${ColorConfig.white}${diff.dataProductName}${ColorConfig.reset}`);
        console.log("");
        console.log(`${ColorConfig.cyan}Changes:${ColorConfig.reset}`);

        if (diff.sections.length === 0) {
            console.log(` ${ColorConfig.white}(no changes)${ColorConfig.reset}`);
            return undefined;
        }

        diff.sections.forEach((section, index) => {
            console.log(
                ` ${ColorConfig.cyan}${this.getScopeLabel(section.scope)}:${ColorConfig.reset} ` +
                `${ColorConfig.white}${section.name}${ColorConfig.reset}`
            );

            section.changes.forEach(change => this.printChange(change));

            if (index < diff.sections.length - 1) {
                console.log("");
            }
        });

        return undefined;
    }

    private printChange(change: DiffChange): void {
        const {symbol, color} = this.getChangePresentation(change.op);
        const label = `${color}${symbol}${ColorConfig.reset} ${ColorConfig.cyan}${change.path}:${ColorConfig.reset}`;

        switch (change.op) {
            case DiffOp.ADD:
                console.log(`   ${label} ${ColorConfig.white}${this.formatValue(change.desired)}${ColorConfig.reset}`);
                return;
            case DiffOp.REMOVE:
                console.log(`   ${label} ${ColorConfig.white}${this.formatValue(change.current)}${ColorConfig.reset}`);
                return;
            case DiffOp.REPLACE:
                console.log(`   ${label}`);
                console.log(`     ${ColorConfig.cyan}Current:${ColorConfig.reset} ${ColorConfig.white}${this.formatValue(change.current)}${ColorConfig.reset}`);
                console.log(`     ${ColorConfig.cyan}Desired:${ColorConfig.reset} ${ColorConfig.white}${this.formatValue(change.desired)}${ColorConfig.reset}`);
                return;
        }
    }

    private getScopeLabel(scope: DiffScope): string {
        switch (scope) {
            case DiffScope.DATA_PRODUCT:
                return "Data Product";
            case DiffScope.RESOURCE:
                return "Resource";
        }
    }

    private getChangePresentation(op: DiffOp): {symbol: string; color: string} {
        switch (op) {
            case DiffOp.ADD:
                return {symbol: "+", color: ColorConfig.green};
            case DiffOp.REMOVE:
                return {symbol: "-", color: ColorConfig.red};
            case DiffOp.REPLACE:
                return {symbol: "~", color: ColorConfig.cyan};
        }
    }

    private formatValue(value: unknown): string {
        if (typeof value === "string") {
            return value;
        }

        if (value === null) {
            return "null";
        }

        if (value === undefined) {
            return "undefined";
        }

        if (typeof value === "object") {
            return JSON.stringify(value);
        }

        return String(value);
    }
}
