import {Metadata} from "./Metadata";
import {Spec} from "./Spec";
export interface DataProduct {
    schemaVersion: string;
    metadata: Metadata;
    spec: Spec;
}