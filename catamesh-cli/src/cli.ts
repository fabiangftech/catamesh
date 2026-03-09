#!/usr/bin/env node

import {TemplateFacade} from "./application/facade/TemplateFacade";
import {Facade} from "./core/facade/Facade";
import {PlanFacade} from "./application/facade/PlanFacade";
import {ApplyFacade} from "./application/facade/ApplyFacade";

const SUCCESS = 0;
const FAILURE = 1;

if (require.main === module) {
    let command: string[] = process.argv.slice(2);
    try {
        switch (command[0]) {
            case "new":
                const templateFacade: Facade<string[],void> = new TemplateFacade();
                templateFacade.run(command);
                break
            case "plan":
                const planFacade: Facade<string[], void> = new PlanFacade();
                planFacade.run(command);
                break
            case "apply":
                const applyFacade: Facade<string[], void> = new ApplyFacade();
                applyFacade.run(command);
                break
            case "get":
                break
            case "diff":
                break
            default:
                break
        }
        process.exit(SUCCESS)
    } catch (e) {
        process.exit(FAILURE)
    }
}
