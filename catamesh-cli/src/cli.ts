#!/usr/bin/env node

import {NewTemplateQuery} from "./infrastructure/cqrs/NewTemplateQuery";
import {Query} from "./core/cqrs/Query";

const SUCCESS = 0;
const FAILURE = 1;

if (require.main === module) {
    const command: string[] = process.argv.slice(2);
    try {
        const  newTemplateQuery: Query<string[], string> = new NewTemplateQuery();
        const output = newTemplateQuery.execute(command);
        console.log('hello world')
        console.log(output)
        process.exit(SUCCESS)
    } catch (e) {
        console.log(e)
        process.exit(FAILURE)
    }
}
