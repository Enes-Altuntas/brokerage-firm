create table assets
(
    asset_name  varchar(255) not null,
    customer_id uuid         not null,
    created_at  timestamp(6) with time zone,
    created_by  varchar(255),
    deleted_at  timestamp(6) with time zone,
    updated_at  timestamp(6) with time zone,
    updated_by  varchar(255),
    size        numeric(38, 2),
    usable_size numeric(38, 2),
    primary key (asset_name, customer_id)
);

create table inbox
(
    id             uuid         not null
        primary key,
    created_at     timestamp(6) with time zone,
    created_by     varchar(255),
    deleted_at     timestamp(6) with time zone,
    updated_at     timestamp(6) with time zone,
    updated_by     varchar(255),
    aggregate_id   uuid         not null,
    aggregate_type varchar(255) not null,
    event_type     varchar(255) not null,
    payload        jsonb
);

create table outbox
(
    id             uuid         not null
        primary key,
    created_at     timestamp(6) with time zone,
    created_by     varchar(255),
    deleted_at     timestamp(6) with time zone,
    updated_at     timestamp(6) with time zone,
    updated_by     varchar(255),
    aggregate_id   uuid         not null,
    aggregate_type varchar(255) not null,
    event_type     varchar(255) not null,
    payload        jsonb
);