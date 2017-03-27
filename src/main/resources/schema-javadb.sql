-- schema-javadb.sql
-- DDL commands for JavaDB/Derby
CREATE TABLE movie (
  id             INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name           VARCHAR(70),
  year           INT,
  classification VARCHAR(50),
  description    VARCHAR(500),
  location       VARCHAR(120)
);

CREATE TABLE customer (
  id          INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name        VARCHAR(50),
  dateOfBirth DATE,
  address     VARCHAR(150),
  email       VARCHAR(50),
  phoneNumber VARCHAR(20)
);

CREATE TABLE lease (
  id                   INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  movieId              INT REFERENCES movie (id) ON DELETE CASCADE,
  customerId           INT REFERENCES customer (id) ON DELETE CASCADE,
  price                INT,
  dateOfRent           DATE,
  expectedDateOfReturn DATE,
  dateOfReturn         DATE
);