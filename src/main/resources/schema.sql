drop table if exists USERS, ITEMS, BOOKINGS, COMMENTS, REQUESTS;

create table if not exists USERS
(
    ID    BIGINT auto_increment
        primary key,
    EMAIL CHARACTER VARYING(255)
        constraint UNIQUE_EMAIL
            unique,
    NAME  CHARACTER VARYING(255)
);

create table if not exists REQUESTS
(
    ID           BIGINT auto_increment
        primary key,
    CREATED      TIMESTAMP,
    DESCRIPTION  CHARACTER VARYING(255),
    REQUESTER_ID BIGINT,
    constraint FKEOAX2T4J9I61P9LMON3009TR4
        foreign key (REQUESTER_ID) references USERS
);

create table if not exists ITEMS
(
    ID          BIGINT auto_increment
        primary key,
    AVAILABLE   BOOLEAN,
    DESCRIPTION CHARACTER VARYING(255),
    NAME        CHARACTER VARYING(255),
    OWNER_ID    BIGINT,
    REQUEST_ID  BIGINT,
    constraint FKE37YI0I6RMAQCQICKVB1VTY22
        foreign key (OWNER_ID) references USERS,
    constraint FKT16U3OEB0G6Y6T9MQ43IDPONC
        foreign key (REQUEST_ID) references REQUESTS
);

create table if not exists BOOKINGS
(
    ID         BIGINT auto_increment
        primary key,
    END_DATE   TIMESTAMP,
    START_DATE TIMESTAMP,
    STATUS     VARCHAR,
    BOOKER_ID  BIGINT,
    ITEM_ID    BIGINT,
    constraint FKBTV44E8P4A4PQ8HFUAKJBTFPC
        foreign key (ITEM_ID) references ITEMS,
    constraint FKC9BNQNREUUVDDCSCMP59FOWIH
        foreign key (BOOKER_ID) references USERS
);

create table if not exists COMMENTS
(
    ID             BIGINT auto_increment
        primary key,
    CREATED        TIMESTAMP,
    TEXT           CHARACTER VARYING(255),
    AUTHOR_NAME_ID BIGINT,
    ITEM_ID        BIGINT,
    constraint FKKBKYDVF8J8TFUEGO2IQXNTWV2
        foreign key (ITEM_ID) references ITEMS,
    constraint FKQK5UN5MCV5LB39Q08MU7VQMK2
        foreign key (AUTHOR_NAME_ID) references USERS
);