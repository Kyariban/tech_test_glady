DROP TABLE IF EXISTS USERS;

CREATE TABLE USERS (
                       username VARCHAR(255) PRIMARY KEY,
                       birth_date DATE NOT NULL,
                       country_of_residence VARCHAR(255) DEFAULT NULL,
                       phone_number varchar(15),
                       gender varchar(6)
);