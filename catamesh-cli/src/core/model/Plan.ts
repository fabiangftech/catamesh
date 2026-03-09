import {PlanResource} from "./PlanResource";
import {PlanAction} from "./PlanAction";

export interface Plan {
    action: PlanAction;
    dataProductName: string;
    requestId: string;
    resources: PlanResource[];
    summary: {
        create: number;
        update: number;
        delete: number;
        replace: number;
        noop: number;
        adopt: number;
    };
}