import {PlanAction} from "./PlanAction";
import {PlanResourceType} from "./PlanResourceType";

export interface PlanResource {
    type: PlanResourceType;
    name: string;
    version: string;
    action: PlanAction;
}