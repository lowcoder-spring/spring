create table properties
(
    id                 uuid         not null constraint properties_pk primary key,
    modified_user      varchar(500),
    created_user       varchar(500),
    last_modified_time timestamp,
    created_time       timestamp,
    application        varchar(255) not null,
    profile            varchar(255),
    label              varchar(255),
    key                varchar(255),
    value              text,
    deleted            boolean default false
);
comment on table properties is '配置库';