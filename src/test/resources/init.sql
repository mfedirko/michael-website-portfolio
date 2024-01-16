CREATE TABLE IF NOT EXISTS ContactRequest (
    objectId VARCHAR PRIMARY KEY,
    createdAt TIMESTAMP NOT NULL,
    updatedAt TIMESTAMP NOT NULL,
    fullName VARCHAR,
    email VARCHAR,
    messageBody VARCHAR
);
