# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table need (
  id                        integer auto_increment not null,
  title                     varchar(255),
  donated_amount            integer,
  ask_amount                integer,
  added_by                  varchar(255),
  description               varchar(255),
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
  confirmation_token        varchar(255),
  constraint pk_user primary key (email))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table need;

drop table token;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

