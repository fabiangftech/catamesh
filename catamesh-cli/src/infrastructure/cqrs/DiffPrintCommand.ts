import {Command} from "../../core/cqrs/Command";
import {ColorConfig} from "../config/ColorConfig";
import {DiffResult} from "../../core/model/DiffResult";
import {DiffTreeNode} from "../../core/model/DiffTreeNode";
import {DiffChangeType} from "../../core/model/DiffChangeType";
import {PolicyRule} from "../../core/model/PolicyRule";

export class DiffPrintCommand implements Command<DiffResult, void>{
    execute(diff: DiffResult): void {
        console.log(`${ColorConfig.cyan}Changes:${ColorConfig.reset}`);

        const changedNodes = this.collectChangedNodes(diff.root);
        if (changedNodes.length === 0) {
            console.log(` ${ColorConfig.white}(no changes)${ColorConfig.reset}`);
        } else {
            changedNodes.forEach(node => this.printChange(node));
        }
        console.log("");
        if (this.printPolicies(diff.policyRules)) {
            console.log("");
        }
        console.log(
            `${ColorConfig.cyan}Diff:${ColorConfig.reset} ` +
            `${ColorConfig.white}${diff.summary.added} added, ${diff.summary.changed} changed, ${diff.summary.removed} removed.${ColorConfig.reset}`
        );
        return undefined;
    }

    private collectChangedNodes(node: DiffTreeNode): Array<DiffTreeNode> {
        const changedNodes = node.changeType === DiffChangeType.NONE ? [] : [node];

        return [
            ...changedNodes,
            ...Object.values(node.fields).flatMap(child => this.collectChangedNodes(child)),
            ...node.elements.flatMap(child => this.collectChangedNodes(child)),
            ...Object.values(node.entries).flatMap(child => this.collectChangedNodes(child)),
        ];
    }

    private printPolicies(policyRules?: PolicyRule[] | null): boolean {
        if (!policyRules || policyRules.length === 0) {
            return false;
        }

        console.log(`${ColorConfig.cyan}Policies:${ColorConfig.reset}`);
        policyRules.forEach(policyRule => this.printPolicy(policyRule));
        return true;
    }

    private printPolicy(policyRule: PolicyRule): void {
        const level = policyRule.level.toUpperCase();
        const color = this.getPolicyLevelColor(policyRule.level);

        console.log(
            `   ${color}! [${level}]${ColorConfig.reset} ` +
            `${ColorConfig.cyan}${policyRule.path}:${ColorConfig.reset} ` +
            `${ColorConfig.white}${policyRule.message}${ColorConfig.reset}`
        );
    }

    private printChange(node: DiffTreeNode): void {
        const {symbol, color} = this.getChangePresentation(node.changeType);
        const label = `${color}${symbol}${ColorConfig.reset} ${ColorConfig.cyan}${node.path}:${ColorConfig.reset}`;

        switch (node.changeType) {
            case DiffChangeType.CREATE:
                console.log(`   ${label} ${ColorConfig.white}${this.formatValue(node.newValue)}${ColorConfig.reset}`);
                return;
            case DiffChangeType.DELETE:
                console.log(`   ${label} ${ColorConfig.white}${this.formatValue(node.oldValue)}${ColorConfig.reset}`);
                return;
            case DiffChangeType.UPDATE:
                console.log(`   ${label}`);
                console.log(`     ${ColorConfig.cyan}Current:${ColorConfig.reset} ${ColorConfig.white}${this.formatValue(node.oldValue)}${ColorConfig.reset}`);
                console.log(`     ${ColorConfig.cyan}Desired:${ColorConfig.reset} ${ColorConfig.white}${this.formatValue(node.newValue)}${ColorConfig.reset}`);
                return;
            case DiffChangeType.NONE:
                return;
        }
    }

    private getChangePresentation(changeType: DiffChangeType): {symbol: string; color: string} {
        switch (changeType) {
            case DiffChangeType.CREATE:
                return {symbol: "+", color: ColorConfig.green};
            case DiffChangeType.DELETE:
                return {symbol: "-", color: ColorConfig.red};
            case DiffChangeType.UPDATE:
                return {symbol: "~", color: ColorConfig.cyan};
            case DiffChangeType.NONE:
                return {symbol: " ", color: ColorConfig.white};
        }
    }

    private getPolicyLevelColor(level: string): string {
        return level.toLowerCase() === "error" ? ColorConfig.red : ColorConfig.white;
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
