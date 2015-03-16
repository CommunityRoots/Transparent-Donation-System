# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table charity (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  constraint pk_charity primary key (id))
;

create table donation (
  id                        bigint auto_increment not null,
  donator_id                bigint,
  amount                    double,
  need_id                   bigint,
  date                      datetime,
  notify                    tinyint(1) default 0,
  constraint pk_donation primary key (id))
;

create table need (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  donated_amount            double,
  ask_amount                double,
  added_by_id               bigint,
  description               varchar(255),
  charity_id                bigint,
  location                  varchar(255),
  full_name                 varchar(255),
  paid_to_charity           tinyint(1) default 0,
  category                  integer,
  urgency                   integer,
  date_added                datetime,
  date_closed               datetime,
  date_paid_to_charity      datetime,
  closed                    tinyint(1) default 0,
  constraint ck_need_category check (category in (0,1)),
  constraint pk_need primary key (id))
;

create table token (
  token                     varchar(255) not null,
  email                     varchar(255),
  date_created              datetime,
  constraint pk_token primary key (token))
;

create table updates (
  id                        bigint auto_increment not null,
  message                   varchar(255),
  date_added                datetime,
  need_id                   bigint,
  constraint pk_updates primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  validated                 tinyint(1) default 0,
  role                      integer,
  charity_id                bigint,
  joined                    datetime,
  last_login                datetime,
  constraint pk_user primary key (id))
;

alter table donation add constraint fk_donation_donator_1 foreign key (donator_id) references user (id) on delete restrict on update restrict;
create index ix_donation_donator_1 on donation (donator_id);
alter table donation add constraint fk_donation_need_2 foreign key (need_id) references need (id) on delete restrict on update restrict;
create index ix_donation_need_2 on donation (need_id);
alter table need add constraint fk_need_addedBy_3 foreign key (added_by_id) references user (id) on delete restrict on update restrict;
create index ix_need_addedBy_3 on need (added_by_id);
alter table need add constraint fk_need_charity_4 foreign key (charity_id) references charity (id) on delete restrict on update restrict;
create index ix_need_charity_4 on need (charity_id);
alter table updates add constraint fk_updates_need_5 foreign key (need_id) references need (id) on delete restrict on update restrict;
create index ix_updates_need_5 on updates (need_id);
alter table user add constraint fk_user_charity_6 foreign key (charity_id) references charity (id) on delete restrict on update restrict;
create index ix_user_charity_6 on user (charity_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table charity;

drop table donation;

drop table need;

drop table token;

drop table updates;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

