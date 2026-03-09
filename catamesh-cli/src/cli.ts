#!/usr/bin/env node

import {CataMeshCoreCommand} from "./infrastructure/cqrs/CataMeshCoreCommand";
import {Query} from "./core/cqrs/Query";
import {TemplateFacade} from "./core/facade/TemplateFacade";
import {DefaultTemplateFacade} from "./application/facade/DefaultTemplateFacade";
import {DefaultPlanFacade} from "./application/facade/DefaultPlanFacade";
import {PlanFacade} from "./core/facade/PlanFacade";

const SUCCESS = 0;
const FAILURE = 1;

if (require.main === module) {
    let command: string[] = process.argv.slice(2);
    try {
        switch (command[0]) {
            case "new":
                const templateFacade: TemplateFacade = new DefaultTemplateFacade();
                templateFacade.newTemplate(command);
                break
            case "plan":
                const planFacade: PlanFacade = new DefaultPlanFacade();
                planFacade.plan(command)
                break
            default:
                break
        }
        process.exit(SUCCESS)
    } catch (e) {
        console.log(e)
        process.exit(FAILURE)
    }
}
