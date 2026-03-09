import {Resource} from "./Resource";

export interface Spec{
    kind:string;
    resources:Array<Resource>
}