  -- schema-psql.sql
  -- DDL commands for PostgreSQL
  DROP TABLE IF EXISTS lease;
  DROP TABLE IF EXISTS customer;
  DROP TABLE IF EXISTS movie;

 CREATE TABLE movie (
   id             SERIAL PRIMARY KEY,
   name           VARCHAR,
   year           INT,
   classification VARCHAR,
   description    VARCHAR,
   location       VARCHAR);

 CREATE TABLE customer (
   id          SERIAL PRIMARY KEY,
   name        VARCHAR,
   dateOfBirth DATE,
   address     VARCHAR,
   email       VARCHAR,
   phoneNumber VARCHAR);

 CREATE TABLE lease (
   id                   SERIAL PRIMARY KEY,
   movieId              INT REFERENCES movie (id) ON DELETE CASCADE ON UPDATE CASCADE,
   customerId           INT REFERENCES customer (id) ON DELETE CASCADE ON UPDATE CASCADE,
   price                INT,
   dateOfRent           DATE,
   expectedDateOfReturn DATE,
   dateOfReturn         DATE);