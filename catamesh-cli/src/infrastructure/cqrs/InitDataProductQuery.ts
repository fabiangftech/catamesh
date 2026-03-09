import {Query} from "../../core/cqrs/Query";

export class InitDataProductQuery implements Query<string, string>{
    execute(input: string): string {
        return "";
    }

}