import {Metadata} from "./Metadata";
import {Spec} from "./Spec";

const json = ```
{
  "metadata": {
    "description": "this is my first data product",
    "displayName": "test--my-first-data-product",
    "domain": "test",
    "id": "0b8ac47b-e2f1-4f28-b3c7-cb4af3f53992",
    "name": "test--my-first-data-product"
  },
  "schemaVersion": "data-product/v1",
  "spec": {
    "kind": "source-aligned",
    "resources": [
      {
        "dataProductId": "0b8ac47b-e2f1-4f28-b3c7-cb4af3f53992",
        "definition": {
          "config": {
            "lifecycleDays": 30
          },
          "schemaVersion": "bucket/v1",
          "version": "0.0.1"
        },
        "displayName": "my-component",
        "id": "4d5e463a-2b8b-44d9-8e1f-9eb3ba2bb492",
        "kind": "bucket",
        "name": "my-component"
      }
    ]
  }
}
```;

export interface DataProduct {
    schemaVersion: string;
    metadata: Metadata;
    spec: Spec;
}