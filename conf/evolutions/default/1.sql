# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table need (
  id                        bigint not null,
  donated_amount            integer,
  ask_amount                integer,
  added_by                  bigint,
  constraint pk_need primary key (id))
;

create table user (
  email                     varchar(255) not null,
  id                        bigint,
  name                      varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (email))
;

create sequence need_seq;

create sequence user_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists need;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists need_seq;

drop sequence if exists user_seq;

