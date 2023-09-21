DROP TABLE IF EXISTS USERS;
DROP TABLE IF EXISTS COMPANY;
DROP TABLE IF EXISTS DEPOSIT;

CREATE TABLE COMPANY (
                         company_id BIGINT PRIMARY KEY,
                         company_name VARCHAR(255) NOT NULL,
                         balance numeric(38,2) NOT NULL
);

CREATE TABLE USERS (
                       user_id BIGINT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL,
                       company_id BIGINT NOT NULL,
                       FOREIGN KEY (company_id) REFERENCES COMPANY(company_id)
);



CREATE TABLE DEPOSIT (
                         deposit_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         company_id BIGINT NOT NULL,
                         deposit_type VARCHAR(31),
                         amount numeric(38,2) NOT NULL,
                         expiration_date timestamp(6),
                         FOREIGN KEY (company_id) REFERENCES COMPANY(company_id),
                         FOREIGN KEY (user_id) REFERENCES USERS(user_id)
);