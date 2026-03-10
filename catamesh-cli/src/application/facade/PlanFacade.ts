import {Query} from "../../core/cqrs/Query";
import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";
import * as fs from "node:fs";
import {Plan} from "../../core/model/Plan";
import {PlanPrintCommand} from "../../infrastructure/cqrs/PlanPrintCommand";
import {Command} from "../../core/cqrs/Command";
import {Facade} from "../../core/facade/Facade";
import {assertDataProductSchema, resolveYamlFileName} from "./DataProductYamlSupport";

export class PlanFacade implements Facade<string[], void> {
    private cataMeshCoreCommand: Query<string[], string> = new CataMeshCoreCommand();
    private planPrintCommand: Command<Plan, void> = new PlanPrintCommand();

    run(command: string[]): void {
        const fileName = resolveYamlFileName(command[1]);
        //todo check if file exist
        const yaml = fs.readFileSync(fileName, "utf8");
        assertDataProductSchema("plan", fileName, yaml);
        this.planDataProduct(command, yaml);
    }

    private planDataProduct(command: string[], yaml: string): void {
        command[1] = yaml;
        const plan: Plan = JSON.parse(this.cataMeshCoreCommand.execute(command));
        //todo check if command have --out
        this.planPrintCommand.execute(plan);
    }
}
