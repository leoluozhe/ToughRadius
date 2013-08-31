create table RAD_USER
(
    USER_NAME VARCHAR(64) NOT NULL,
    PASSWORD VARCHAR(64) NOT NULL,
    GROUP_NAME VARCHAR(64) NOT NULL
);
alter table RAD_USER add constraint PK_RAD_USER primary key (USER_NAME);

create table RAD_USER_META
(
    USER_NAME VARCHAR(64) NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    VALUE VARCHAR(255) NOT NULL
);
alter table RAD_USER_META add constraint PK_RAD_USER_META primary key (USER_NAME,NAME);
alter table RAD_USER_META add constraint FK_RAD_USER_META foreign key (USER_NAME) references RAD_USER (USER_NAME);

create table RAD_GROUP
(
    GROUP_NAME VARCHAR(64) NOT NULL,
    GROUP_DESC VARCHAR(255) NOT NULL
);
alter table RAD_GROUP add constraint PK_RAD_GROUP primary key (GROUP_NAME);

create table RAD_GROUP_META
(
    GROUP_NAME VARCHAR(64) NOT NULL,
    NAME VARCHAR(255) NOT NULL,
    VALUE VARCHAR(255) NOT NULL
);
alter table RAD_GROUP_META add constraint PK_RAD_GROUP_META primary key (GROUP_NAME,NAME);
alter table RAD_GROUP_META add constraint FK_RAD_GROUP_META foreign key (GROUP_NAME) references RAD_GROUP (GROUP_NAME);

create table RAD_CLIENT
(
    ADDRESS VARCHAR(128) NOT NULL,
    SECRET VARCHAR(128) NOT NULL,
    CLIENT_DESC VARCHAR(255) NOT NULL
);
alter table RAD_CLIENT add constraint PK_RAD_CLIENT primary key (ADDRESS);


create table RAD_BLACKLIST
(
    MACADDR VARCHAR(128) NOT NULL,
    EXPIRE INTEGER
);
alter table RAD_BLACKLIST add constraint PK_RAD_BLACKLIST primary key (MACADDR);
