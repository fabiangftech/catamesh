# What is CataMesh? — Architecture

**CataMesh** is a framework and control plane for defining, managing, and evolving **data products** using a declarative approach. It enables teams to describe data infrastructure and resources as code, apply changes safely, and maintain a consistent catalog of data products.

CataMesh is designed to manage data platforms at scale by treating data products as versioned artifacts that can be diffed, planned, and applied in a controlled workflow.

Here, we explain the key architectural concepts behind CataMesh.

---

# Declarative Data Products

In CataMesh, every **data product** is defined declaratively using a configuration file (for example, YAML).
This file describes metadata, resources, and specifications that represent the data product.

Instead of manually creating infrastructure or configurations, teams declare the **desired state** of the data product.

For example, a data product may include:

* storage resources such as buckets or tables
* streaming components
* schemas and definitions
* metadata describing ownership and domain

CataMesh then evaluates the difference between the **current state** and the **desired state**.

This allows the system to safely determine what changes are required.

---

# Diff, Plan, Apply Workflow

CataMesh uses a workflow inspired by modern infrastructure tools to safely manage changes.

### Diff

The **diff** step compares the declared data product configuration with the current state stored in the control plane.

It identifies:

* resources to be created
* resources to be updated
* resources to be removed

This provides visibility into the exact differences before any action is taken.

---

### Plan

The **plan** step converts the detected differences into a sequence of execution steps.

Each step represents an action that must be performed to reconcile the current state with the desired state.

For example:

* updating metadata
* creating a new resource
* modifying a resource definition
* deleting obsolete components

The plan ensures that changes are explicit, predictable, and reviewable before execution.

---

### Apply

The **apply** step executes the plan.

During this phase, CataMesh updates the control plane state and persists the new configuration of the data product.

Depending on the platform integration, this step may also trigger infrastructure provisioning or updates in external systems.

---

# Manage Data Products at Scale

CataMesh is designed to support organizations with **large numbers of data products and teams**.

The architecture enables:

* versioned data product definitions
* reproducible deployments
* clear ownership and governance
* automated change tracking

Because data products are treated as structured resources, organizations can manage hundreds or thousands of data products consistently.

---

# Extensible Resource Model

CataMesh separates the **data product definition** from the **resource implementation**.

Each resource references a **resource definition**, which describes how that resource should behave.

This allows platforms to evolve independently and enables teams to reuse standardized resource definitions across multiple data products.

Examples of resources may include:

* storage buckets
* streaming topics
* compute pipelines
* data schemas

This modular architecture allows CataMesh to integrate with many data platform technologies.

---

# A Control Plane for the Data Platform

CataMesh acts as a **control plane** for data products.

Instead of managing infrastructure manually, teams interact with CataMesh to declare and evolve data products. The platform keeps track of changes, enforces policies, and ensures that the data platform remains consistent.

By treating data products as declarative artifacts, CataMesh enables organizations to scale their data platforms with the same principles used in modern infrastructure and software engineering.