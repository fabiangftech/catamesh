import {Command} from "../../core/cqrs/Command";
import {Plan} from "../../core/model/Plan";
import {ColorConfig} from "../config/ColorConfig";

export class PlanPrintCommand implements Command<Plan, void>{
    execute(plan: Plan): void {
        console.log(`${ColorConfig.cyan}Plan:${ColorConfig.reset} ${ColorConfig.white}${plan.summary.create} to create, ${ColorConfig.white}${plan.summary.update} to update, ${ColorConfig.white}${plan.summary.delete} to delete, ${ColorConfig.white}${plan.summary.noop} to noop.`);
        console.log(`${ColorConfig.cyan}Data Product:${ColorConfig.white} ${plan.dataProductName}`);
        console.log(`${ColorConfig.cyan}Request ID:${ColorConfig.reset} ${ColorConfig.white}${plan.requestId}${ColorConfig.reset}`);
        console.log("");
        console.log(`${ColorConfig.cyan}Resources:${ColorConfig.reset}`);
        plan.resources.forEach(r => {
            console.log(` ${ColorConfig.green}+${ColorConfig.reset} ${r.type}.${ColorConfig.white}${r.name}${r.version ? ` ${ColorConfig.white}(v${r.version})${ColorConfig.reset}` : ""}`);
        });
        return undefined;
    }
}