import {CataMeshCoreCommand} from "../../infrastructure/cqrs/CataMeshCoreCommand";

export class DefaultPlanFacade implements PlanFacade {

    constructor(private cataMeshCoreCommand: CataMeshCoreCommand = new CataMeshCoreCommand()) {
    }

    plan(command: string[]): void {
        this.cataMeshCoreCommand.execute(command)
    }
}