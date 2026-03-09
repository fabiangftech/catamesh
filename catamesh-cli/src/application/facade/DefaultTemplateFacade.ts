import {TemplateFacade} from "../../core/facade/TemplateFacade";
import {Query} from "../../core/cqrs/Query";
import {NewTemplateQuery} from "../../infrastructure/cqrs/NewTemplateQuery";
import * as fs from "node:fs";

export class DefaultTemplateFacade implements TemplateFacade {

    private newTemplateQuery: Query<string[], string> = new NewTemplateQuery();

    newTemplate(command: string[]): void {
        //todo validate input
        const yaml: string = this.newTemplateQuery.execute(command);
        fs.writeFileSync(command[2] + ".yaml", yaml, "utf8");
    }
}