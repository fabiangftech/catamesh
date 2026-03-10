#!/usr/bin/env node

import {TemplateFacade} from "./application/facade/TemplateFacade";
import {Facade} from "./core/facade/Facade";
import {PlanFacade} from "./application/facade/PlanFacade";
import {ApplyFacade} from "./application/facade/ApplyFacade";
import {GetFacade} from "./application/facade/GetFacade";
import {CataMeshCoreError} from "./core/exception/CataMeshCoreError";
import {ColorConfig} from "./infrastructure/config/ColorConfig";
import {DiffFacade} from "./application/facade/DiffFacade";

const SUCCESS = 0;
const FAILURE = 1;

type FacadeFactory = () => Facade<string[], void>;

export interface RunCliDependencies {
    applyFacadeFactory?: FacadeFactory;
    diffFacadeFactory?: FacadeFactory;
    getFacadeFactory?: FacadeFactory;
    planFacadeFactory?: FacadeFactory;
    templateFacadeFactory?: FacadeFactory;
    writeError?: (message: string) => void;
}

export function runCli(
    originalCommand: string[],
    dependencies: RunCliDependencies = {},
): number {
    const command: string[] = [...originalCommand];
    const writeError = dependencies.writeError ?? ((message: string) => console.error(message));

    try {
        switch (command[0]) {
            case "new":
                (dependencies.templateFacadeFactory ?? (() => new TemplateFacade()))().run(command);
                break
            case "plan":
                (dependencies.planFacadeFactory ?? (() => new PlanFacade()))().run(command);
                break
            case "apply":
                (dependencies.applyFacadeFactory ?? (() => new ApplyFacade()))().run(command);
                break
            case "get":
                (dependencies.getFacadeFactory ?? (() => new GetFacade()))().run(command);
                break
            case "diff":
                (dependencies.diffFacadeFactory ?? (() => new DiffFacade()))().run(command);
                break
            case "help":
                break;
            default:
                break
        }
        return SUCCESS;
    } catch (e: unknown) {
        const executedCommand = originalCommand.length > 0 ? originalCommand.join(" ") : "(none)";
        if (e instanceof CataMeshCoreError) {
            writeError(
                `${ColorConfig.red}Command failed${ColorConfig.reset}\n` +
                `${ColorConfig.red}Code:${ColorConfig.reset} ${ColorConfig.white}${e.errorCode} (${e.status})${ColorConfig.reset}\n` +
                `${ColorConfig.red}Command:${ColorConfig.reset} ${ColorConfig.white}cata ${executedCommand}${ColorConfig.reset}\n` +
                `${ColorConfig.red}Reason:${ColorConfig.reset} ${ColorConfig.white}${e.title}${ColorConfig.reset}\n` +
                `${ColorConfig.red}Message:${ColorConfig.reset} ${ColorConfig.white}${e.message.trim()}${ColorConfig.reset}` +
                renderHint(e) +
                renderDetails(e)
            );
            return e.status ?? FAILURE;
        } else if (e instanceof Error) {
            writeError(`Error: ${e.message}`);
        } else {
            writeError(`Error: ${String(e)}`);
        }
        return FAILURE;
    }
}

function renderHint(error: CataMeshCoreError): string {
    if (!error.hint) {
        return "";
    }
    return `\n${ColorConfig.red}Hint:${ColorConfig.reset} ${ColorConfig.white}${error.hint}${ColorConfig.reset}`;
}

function renderDetails(error: CataMeshCoreError): string {
    if (error.details.length === 0) {
        return "";
    }

    const renderedDetails = error.details
        .map((detail) => `${ColorConfig.white}- ${detail}${ColorConfig.reset}`)
        .join("\n");

    return `\n${ColorConfig.red}Details:${ColorConfig.reset}\n${renderedDetails}`;
}

if (require.main === module) {
    process.exit(runCli(process.argv.slice(2)));
}
