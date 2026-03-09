import {ResourceDefinition} from "./ResourceDefinition";

export interface Resource {
    "dataProductId": string;
    "definition": ResourceDefinition
    "displayName": string;
    "id": string;
    "kind": string;
    "name": string;
}