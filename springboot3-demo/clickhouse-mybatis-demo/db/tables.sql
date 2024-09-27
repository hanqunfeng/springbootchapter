-- `default`.user_info definition

CREATE TABLE default.user_info
(

    `id` Int64,

    `username` String,

    `addr` String,

    `create_time` DateTime64(3)
)
    ENGINE = MergeTree
    PRIMARY KEY id
ORDER BY id
SETTINGS index_granularity = 8192;
