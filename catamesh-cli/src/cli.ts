#!/usr/bin/env node

import {NewTemplateQuery} from "./infrastructure/cqrs/NewTemplateQuery";
import {Query} from "./core/cqrs/Query";
import {TemplateFacade} from "./core/facade/TemplateFacade";
import {DefaultTemplateFacade} from "./application/facade/DefaultTemplateFacade";

const SUCCESS = 0;
const FAILURE = 1;

if (require.main === module) {
    const command: string[] = process.argv.slice(2);
    try {
        console.log(command)
        switch (command[0]) {
            case "new":
                const templateFacade: TemplateFacade = new DefaultTemplateFacade();
                templateFacade.newTemplate(command);
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
