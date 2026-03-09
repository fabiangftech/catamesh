import {PlanResource} from "./PlanResource";

export interface Plan {
    action: string;
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