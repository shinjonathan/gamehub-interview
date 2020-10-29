create table game
(
    id       UUID not null default random_uuid() primary key,
    name     varchar,
    platform varchar,
    creator  varchar
);

create table gamer
(
    id UUID not null default random_uuid() primary key,
    first_name varchar,
    last_name varchar,
    email varchar /* TODO: unique? */
);

create table score
(
    id bigint not null auto_increment primary key,
    gamer_id UUID not null references gamer(id), /* TODO: Index */
    game_id UUID not null references game(id), /* TODO: Index */
    score int
    /* TODO: Should we add time here? */
);

insert into game values ('55364F28-3670-451D-9BD4-ADC265769B0A', 'Dead By Daylight', 'PC', 'BEHAVIOR');
insert into game values ('32EA6860-7D17-4792-874F-081D35ED004C', 'CS:GO', 'PC', 'VALVE');
insert into gamer values ('AC598975-31EB-429C-864B-443899EAC48B', 'Jonathan', 'Shin', 'jonathanwshin@gmail.com');
insert into gamer values ('981B3989-E5EA-49E2-B961-5B7130D672C8', 'John', 'Doe', 'john@doe.com');
insert into score values (0, 'AC598975-31EB-429C-864B-443899EAC48B', '55364F28-3670-451D-9BD4-ADC265769B0A', 3);
insert into score values (1, '981B3989-E5EA-49E2-B961-5B7130D672C8', '55364F28-3670-451D-9BD4-ADC265769B0A', 2);
insert into score values (2, 'AC598975-31EB-429C-864B-443899EAC48B', '55364F28-3670-451D-9BD4-ADC265769B0A', 5);
insert into score values (3, 'AC598975-31EB-429C-864B-443899EAC48B', '32EA6860-7D17-4792-874F-081D35ED004C', 5);



