create table person
(
    id bigserial not null
        constraint person_pk
            primary key,
    first_name varchar(255),
    last_name varchar(255)
);
alter table person owner to sebas;
create sequence person_id_seq;
alter sequence person_id_seq owner to sebas;
