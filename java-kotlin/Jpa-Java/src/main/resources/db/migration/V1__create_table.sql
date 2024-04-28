create table persons_pk_auto_in (
     person_id int not null auto_increment,
     last_name varchar(255) not null,
     first_name varchar(255),
     age int,
     primary key (person_id)
);

create table persons_pk_table (
     person_id int not null,
     last_name varchar(255) not null,
     first_name varchar(255),
     age int,
     primary key (person_id)
);

create table persons_pk_seq (
      person_id int not null,
      last_name varchar(255) not null,
      first_name varchar(255),
      age int,
      primary key (person_id)
);