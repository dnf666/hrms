create table booklend
(
  company_id  varchar(30)                         not null,
  book_record    varchar(50)                         not null,
  book_name   varchar(50)                         not null,
  lend_time   timestamp  not null,
  return_time timestamp  not null,
  borrower    varchar(20)                         not null,
  primary key (company_id, book_record)
);

create table library
(
  company_id varchar(30) not null,
  book_id varchar (50) not null,
  book_name  varchar(50) not null,
  category   varchar(10) not null,
  num        int         not null,
  version    varchar(20) not null,
  primary key (company_id, book_name, version)
);
