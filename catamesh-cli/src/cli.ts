#!/usr/bin/env node

import {TemplateFacade} from "./application/facade/TemplateFacade";
import {Facade} from "./core/facade/Facade";
import {PlanFacade} from "./application/facade/PlanFacade";
import {ApplyFacade} from "./application/facade/ApplyFacade";
import {GetFacade} from "./application/facade/GetFacade";
import {CataMeshCoreError} from "./core/exception/CataMeshCoreError";
import {ColorConfig} from "./infrastructure/config/ColorConfig";

const SUCCESS = 0;
const FAILURE = 1;

if (require.main === module) {
    const originalCommand: string[] = process.argv.slice(2);
    let command: string[] = process.argv.slice(2);
    try {
        switch (command[0]) {
            case "new":
                const templateFacade: Facade<string[], void> = new TemplateFacade();
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
                const getFacade: Facade<string[], void> = new GetFacade();
                getFacade.run(command);
                break
            case "diff":
                break
            default:
                break
        }
        process.exit(SUCCESS)
    } catch (e: unknown) {
        const executedCommand = originalCommand.length > 0 ? originalCommand.join(" ") : "(none)";
        if (e instanceof CataMeshCoreError) {
            console.error(
                `${ColorConfig.white}Command failed\nStatus: ${e.status}\nCommand: ${executedCommand}\nError: ${e.message.trim()}`
            );
        } else if (e instanceof Error) {
            console.error(`Error: ${e.message}`);
        } else {
            console.error(`Error: ${String(e)}`);
        }
        process.exit(FAILURE)
    }
}
