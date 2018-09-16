create table project
(
  company_id   varchar(30)                         not null,
  project_id   int auto_increment,
  project_name varchar(50)                         not null,
  project_url  varchar(100)                        not null,
  online_time  timestamp  not null,
  primary key (project_id, company_id)
);