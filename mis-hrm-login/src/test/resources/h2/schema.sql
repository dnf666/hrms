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