create table NOTIFICATIONS(
    ID INT  NOT NULL generated always as identity,
    NAME TEXT NOT NULL,
    MESSAGE TEXT,
    CONSTRAINT NOTIFICATION_PK PRIMARY KEY (ID),
    UNIQUE (NAME)
);

insert into NOTIFICATIONS(name, message) values ('Cashier Notification', '<p>Message 1</p>');


create table NOTIFICATIONS_LOG(
    ID INT  NOT NULL GENERATED ALWAYS AS IDENTITY,
    NAME TEXT NOT NULL,
    MESSAGE TEXT,
    SEND_DATE timestamptz default now(),
    CONSTRAINT NOTIFICATION_LOG_PK PRIMARY KEY (ID)
);