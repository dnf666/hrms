create table booklend
(
  company_id  varchar(30)                         not null,
  book_id     varchar(50)                         not null,
  book_name   varchar(50)                         not null,
  lend_time   timestamp  not null
  on update CURRENT_TIMESTAMP,
  return_time timestamp  not null
  on update CURRENT_TIMESTAMP,
  borrower    varchar(20)                         not null,
  primary key (company_id, book_id)
);

create table company
(
  email             varchar(30)  not null
    primary key,
  name              varchar(100) not null,
  applicant_name    varchar(20)  not null,
  organization_size varchar(20)  not null,
  main_category     varchar(20)  not null,
  vice_category     varchar(20)  not null,
  password          varchar(100) not null
);

create table `index`
(
  company_id varchar(30)  null,
  column_2   int          null,
  outline    varchar(100) null,
  photo_path varchar(100) null
);

create table library
(
  company_id varchar(30) not null,
  book_name  varchar(50) not null,
  category   varchar(10) not null,
  num        int         not null,
  version    varchar(20) not null,
  primary key (company_id, book_name, version)
);

create table member
(
  company_id   varchar(30) not null,
  num          varchar(50) not null,
  name         varchar(50) not null,
  phone_number varchar(50) not null,
  email        varchar(20) not null,
  grade        varchar(20) not null,
  sex          varchar(2)  not null,
  profession   varchar(20) not null,
  department   varchar(50) not null,
  primary key (company_id, num)
);

create table project
(
  company_id   varchar(30)                         not null,
  project_id   int auto_increment,
  project_name varchar(50)                         not null,
  project_url  varchar(100)                        not null,
  online_time  timestamp  not null
  on update CURRENT_TIMESTAMP,
  primary key (project_id, company_id)
);

create table whereabout
(
  company_id   varchar(30)  not null,
  num          varchar(50)  not null,
  name         varchar(50)  not null,
  phone_number varchar(50)  not null,
  email        varchar(20)  not null,
  grade        int          not null,
  sex          varchar(2)   not null,
  profession   varchar(20)  not null,
  department   varchar(50)  not null,
  work_place   varchar(100) not null,
  primary key (company_id, num)
);

