CREATE TABLE users(
    id       INT PRIMARY KEY,
    login    VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);