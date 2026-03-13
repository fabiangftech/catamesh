import {Query} from "../../core/cqrs/Query";

export class GetKindFromContentQuery implements Query<string, string> {
    execute(content: string): string {
        if (content.includes("data-product/v1")) {
            return "data-product";
        }else  if (content.includes("environment/v1")) {
            return "environment";
        }
        throw new Error("schemaVersion not valid in file!")
    }
}