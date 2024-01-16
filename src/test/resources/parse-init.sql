CREATE TABLE IF NOT EXISTS ContactRequest (
    objectId VARCHAR PRIMARY KEY,
    createdAt TIMESTAMP NOT NULL,
    updatedAt TIMESTAMP NOT NULL,
    fullName VARCHAR,
    email VARCHAR,
    messageBody VARCHAR
);

CREATE TABLE IF NOT EXISTS Lesson (
    objectId VARCHAR PRIMARY KEY,
    createdAt TIMESTAMP NOT NULL,
    updatedAt TIMESTAMP NOT NULL,
    title VARCHAR,
    author VARCHAR,
    category VARCHAR,
    description VARCHAR
);
