import {Command} from "../../core/cqrs/Command";
import {ApplyResult} from "../../core/model/ApplyResult";
import {ColorConfig} from "../config/ColorConfig";
import {DataProductPrintCommand} from "./DataProductPrintCommand";

export class ApplyPrintCommand implements Command<ApplyResult, void> {
    private dataProductPrintCommand: Command<ApplyResult["dataProduct"], void> = new DataProductPrintCommand();

    execute(result: ApplyResult): void {
        console.log(
            `${ColorConfig.cyan}Apply:${ColorConfig.reset} ` +
            `${ColorConfig.white}${result.plan.summary.create} to create, ` +
            `${result.plan.summary.update} to update, ` +
            `${result.plan.summary.delete} to delete, ` +
            `${result.plan.summary.noop} to noop.${ColorConfig.reset}`
        );
        console.log(`${ColorConfig.cyan}Action:${ColorConfig.reset} ${ColorConfig.white}${result.plan.action}${ColorConfig.reset}`);
        console.log(`${ColorConfig.cyan}Data Product:${ColorConfig.reset} ${ColorConfig.white}${result.plan.dataProductName}${ColorConfig.reset}`);
        console.log(`${ColorConfig.cyan}Request ID:${ColorConfig.reset} ${ColorConfig.white}${result.plan.requestId}${ColorConfig.reset}`);
        console.log("");
        console.log(`${ColorConfig.cyan}Final State:${ColorConfig.reset}`);
        this.dataProductPrintCommand.execute(result.dataProduct);
    }
}
