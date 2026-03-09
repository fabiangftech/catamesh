create table if not exists data_product
(
    id             varchar(50)  not null,
    schema_version varchar(50)  not null,
    name           varchar(100) not null unique,
    display_name   varchar(100) not null,
    kind           varchar(30)  not null,
    domain         varchar(50),
    description    varchar(300),

    constraint pk_data_product_id primary key (id)
);

create table if not exists resource
(
    id              varchar(50)  not null,
    data_product_id varchar(50)  not null,
    name            varchar(100) not null,
    display_name    varchar(100) not null,
    kind            varchar(30)  not null,

    constraint pk_resource_id primary key (id)
);

create table if not exists resource_definition
(
    id             varchar(50) not null,
    resource_id    varchar(50) not null,
    schema_version varchar(50) not null,
    version        varchar(50),
    active         boolean default true,
    config         jsonb,
    constraint pk_resource_definition_id primary key (id)
);

create unique index if not exists ux_resource_data_product_id_name
    on resource (data_product_id, name);

create unique index if not exists ux_resource_id_version
    on resource_definition (resource_id, version);
