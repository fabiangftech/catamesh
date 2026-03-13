#!/usr/bin/env node

import {DefaultPlanFacade} from "./application/facade/DefaultPlanFacade";

const planFacade: PlanFacade = new DefaultPlanFacade();

if (require.main === module) {
    const command: string[] = process.argv.slice(2);
    console.log(command)
    planFacade.plan(command);
}