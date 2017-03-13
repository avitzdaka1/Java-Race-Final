/* Java race database tables */
/* grant all privileges on *.* to 'scott'@'localhost' identified by 'tiger'; */

drop database if exists javarace;
create database javarace
use javarace

drop table if exists Gambler;
drop table if exists Race;
drop table if exists Car;
drop table if exists RaceResult;
drop table if exists GamblerCarRace;

create table Gambler (id integer not null unique, name varchar(10) not null unique, password varchar(10) not null, balance integer, constraint pkId primary key (id));
create table Race (number integer not null unique, date raceDate, state integer, totalBets integer, constraint pkNumber primary key (number));
create table Car (name varchar(10) not null unique, make varchar(10) not null, size varchar(10) not null, color varchar(10) not null, type varchar(10) not null);
create table RaceResult(raceNumber integer not null, gamblerId integer not null, revenue integer, position integer, constraint fkRaceNumber foreign key (raceNumber) referencees Race(number), constraint fkGamblerId foreign key (gamblerId) references Gambler(id));
create table GamblerCarRace(gamblerId integer not null, raceNumber integer not null, carName varchar(10) not null, bet integer, constraint fkGamblerId foreign key (gamblerId) referencees Gambler(id), constraint fkRaceNumber foreign key (raceNumber) referencees Race(number), constraint fkCarName foreign key (carName) references Car(name));

