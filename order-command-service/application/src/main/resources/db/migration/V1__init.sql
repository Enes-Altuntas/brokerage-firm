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

create table orders
(
    id          uuid not null
        primary key,
    created_at  timestamp(6) with time zone,
    created_by  varchar(255),
    deleted_at  timestamp(6) with time zone,
    updated_at  timestamp(6) with time zone,
    updated_by  varchar(255),
    asset_name  varchar(255),
    customer_id uuid,
    price       numeric(38, 2),
    side        varchar(255)
        constraint orders_side_check
            check ((side)::text = ANY ((ARRAY ['BUY'::character varying, 'SELL'::character varying])::text[])),
    size        numeric(38, 2),
    matched_size        numeric(38, 2),
    status      varchar(255)
        constraint orders_status_check
            check ((status)::text = ANY
                   ((ARRAY ['INIT'::character varying, 'PENDING'::character varying, 'REJECTED'::character varying, 'MATCHED'::character varying, 'PARTIALLY_MATCHED'::character varying, 'CANCEL_REQUESTED'::character varying, 'CANCELED'::character varying])::text[]))
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