/* tranformado en postgresql */

create table country (
code varchar(2) not null primary key, -- iso
name varchar(100) not null
);

create table region (
    code varchar(2) not null primary key,
    name varchar(100) not null,
    countryCode varchar(2) not null references country(code)
);

create table province (
    code varchar(2) not null primary key,
    name varchar(100) not null,
    -- ISO 3166-2:ES
    iso varchar(5) not null,
    countryCode varchar(2) not null references country(code),
    regionCode varchar(2) not null references region(code)
);

create table city (
    code varchar(5) not null primary key,
    name varchar(100) not null,
    provinceCode varchar(2) not null references province(code)
);

create table holyday (
    id SERIAL primary key,
    day date not null,
    name varchar(100) not null,
    city varchar(5) null references city(code),
    region varchar(2) null references region(code),
    country varchar(2) not null references country(code)
);

create table configuration (
    entryKey varchar(100) not null primary key,
    entryValue varchar(255) null,
    comment varchar(250) null
)