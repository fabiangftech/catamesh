import {PlanFacade} from "../../core/facade/PlanFacade";
import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import * as fs from "node:fs";
import {Plan} from "../../core/model/Plan";
import {PlanPrintCommand} from "../../infrastructure/cqrs/PlanPrintCommand";
import {Command} from "../../core/cqrs/Command";
import {Schema} from "../../core/model/Schema";
import {ColorConfig} from "../../infrastructure/config/ColorConfig";

export class DefaultPlanFacade implements PlanFacade {
    private cataMeshCoreCommand: Query<string[], string> = new CataMeshCoreCommand();
    private planPrintCommand: Command<Plan, void> = new PlanPrintCommand();

    plan(command: string[]): void {
        const fileName: string = command[1].includes(".yml") ? command[1] : command[1] + ".yaml";
        console.log(fileName)
        //todo check if file exist
        const yaml: string = fs.readFileSync(fileName, "utf8");
        if (yaml.includes(Schema.data_product_v1)) {
            this.planDataProduct(command, yaml);
        } else {
            console.log(`${ColorConfig.white}Invalid schema version in ${command[1]}.yaml`);
        }
    }

    private planDataProduct(command: string[], yaml: string): void {
        command[1] = yaml;
        const plan: Plan = JSON.parse(this.cataMeshCoreCommand.execute(command));
        //todo check if command have --out
        this.planPrintCommand.execute(plan);
    }
}