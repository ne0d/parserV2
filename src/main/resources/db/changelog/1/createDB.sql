--liquibase add sql
--changeset tsarkov
create table tree_table (
                    node_id int not null primary key,
                    parent_id int references tree_table( node_id ),
                    node_name varchar(100),
                    node_value varchar(1000)
);
--rollback drop table tree_table;