
create table member
(
  company_id varchar(30) not null,
  num varchar(50) not null,
  name varchar(50) not null,
  phone_number varchar(50) not null,
  email varchar(50) not null,
  grade varchar(20) not null,
  sex varchar(2) not null,
  profession varchar(20) not null,
  department varchar(50) not null,
  primary key (company_id, num)
)
;