create table IF NOT EXISTS USERS
(
    ID    BIGINT auto_increment
        primary key,
    EMAIL CHARACTER VARYING(255)
        constraint UNIQUE_EMAIL
            unique,
    NAME  CHARACTER VARYING(255)
);

create table IF NOT EXISTS ITEMS
(
    ID          BIGINT auto_increment
        primary key,
    AVAILABLE   BOOLEAN,
    DESCRIPTION CHARACTER VARYING(255),
    NAME        CHARACTER VARYING(255),
    OWNER_ID    BIGINT,
    constraint FKE37YI0I6RMAQCQICKVB1VTY22
        foreign key (OWNER_ID) references USERS
);

create table IF NOT EXISTS BOOKINGS
(
    ID         BIGINT auto_increment
        primary key,
    END_DATE   TIMESTAMP,
    START_DATE TIMESTAMP,
    STATUS     INTEGER,
    BOOKER_ID  BIGINT,
    ITEM_ID    BIGINT,
    constraint FKBTV44E8P4A4PQ8HFUAKJBTFPC
        foreign key (ITEM_ID) references ITEMS,
    constraint FKC9BNQNREUUVDDCSCMP59FOWIH
        foreign key (BOOKER_ID) references USERS
);

create table IF NOT EXISTS COMMENTS
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


