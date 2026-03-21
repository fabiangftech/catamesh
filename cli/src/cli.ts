#!/usr/bin/env node

import {DefaultCataMeshFacade} from "./application/facade/DefaultCataMeshFacade";

const cataMeshFacade: CataMeshFacade = new DefaultCataMeshFacade();

if (require.main === module) {
    const command: string[] = process.argv.slice(2);
    switch (command[0]) {
        case "init":
            cataMeshFacade.init(command);
            break;
        case "validate":
            cataMeshFacade.validate(command);
            break;
        case "diff":
            cataMeshFacade.diff(command);
            break;
        case "plan":
            cataMeshFacade.plan(command);
            break;
        case "apply":
            cataMeshFacade.apply(command);
            break;
        case "get":
            cataMeshFacade.get(command);
            break;
        case "pull":
            cataMeshFacade.pull(command);
            break;
        default:
            break;
    }
}