# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table donation (
  id                        bigint auto_increment not null,
  need_id                   bigint,
  donator_email             varchar(255),
  amount                    double,
  constraint pk_donation primary key (id))
;

create table need (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  donated_amount            double,
  ask_amount                double,
  added_by                  varchar(255),
  description               varchar(255),
  charity                   varchar(255),
  location                  varchar(255),
  full_name                 varchar(255),
  urgency                   integer,
  date_added                datetime,
  constraint pk_need primary key (id))
;

create table token (
  token                     varchar(255) not null,
  user_id                   bigint,
  email                     varchar(255),
  date_creation             datetime,
  constraint pk_token primary key (token))
;

create table user (
  email                     varchar(255) not null,
  id                        bigint,
  first_name                varchar(255),
  last_name                 varchar(255),
  password                  varchar(255),
  role                      varchar(255),
  charity                   varchar(255),
  constraint pk_user primary key (email))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table donation;

drop table need;

drop table token;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

