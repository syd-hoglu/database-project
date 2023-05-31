-- CS 4322 Project
-- Sydney Hoglund and Jacob Rohde
-- 4/14/23


CREATE TABLE client(
    client_id VARCHAR(10),
    client_name VARCHAR(20) NOT NULL,
    email VARCHAR(50) NOT NULL,
    PRIMARY KEY (client_id)
);

INSERT INTO client VALUES ('aaaaaaaaaa', 'Billy Ashley', 'billash@hotmail.gov');
INSERT INTO client VALUES ('bbbbbbbbbb', 'Catherine Fry', 'cat.fry@gmail.org');
INSERT INTO client VALUES ('cccccccccc', 'Mark Beard', 'beardy@yahoo.com');
INSERT INTO client VALUES ('dddddddddd', 'Stephanie Horn', 'stephanie.horn@gmail.org');

CREATE TABLE receipt(
    receipt_id NUMERIC (10,0),
    date_purchased DATE NOT NULL,
    amount NUMERIC (8,2) NOT NULL,
    salesperson VARCHAR (20) NOT NULL,
    purchaser VARCHAR (10) NOT NULL,
    FOREIGN KEY (purchaser) REFERENCES client,
    PRIMARY KEY (receipt_id)
);

INSERT INTO receipt VALUES (0000000000, '4/19/1999', 300.99, 'George', 'bbbbbbbbbb');
INSERT INTO receipt VALUES (0000000001, '4/20/2007', 2000.99, 'Marie', 'cccccccccc');
INSERT INTO receipt VALUES (0000000002, '4/21/2021', 99999.99, 'George', 'dddddddddd');

CREATE TABLE piano(
    serial_number VARCHAR(10),
    make VARCHAR (20) NOT NULL,
    model VARCHAR (20) NOT NULL,
    year CHAR (4) NOT NULL,
    msrp NUMERIC (8,2),
    trade_in NUMERIC (10,0),
    FOREIGN KEY (trade_in) REFERENCES receipt,
    PRIMARY KEY (serial_number)
);

INSERT INTO piano VALUES ('a100000000', 'Yamaha', 'Clavinova', '1987', 1999.99, NULL);
INSERT INTO piano VALUES ('b200000000', 'Casio', 'Celviano', '1963', 1600.99, NULL);
INSERT INTO piano VALUES ('c300000000', 'Steinway', 'Queen Anne', '1475', 34900.00, NULL);
INSERT INTO piano VALUES ('d400000000', 'Sonnova', 'Luminos', '1812', 12999.99, 0000000001);

CREATE TABLE tunings(
    piano_tuned VARCHAR(10),
    date_tuned DATE,
    tuner VARCHAR (20),
    FOREIGN KEY (piano_tuned) REFERENCES piano,
    PRIMARY KEY (piano_tuned, date_tuned)
);

INSERT INTO tunings VALUES ('a100000000', '1/11/2002', 'Kyle Hester');
INSERT INTO tunings VALUES ('b200000000', '5/29/2009', 'Kyle Hester');
INSERT INTO tunings VALUES ('c300000000', '7/6/2016', 'Kyle Hester');
INSERT INTO tunings VALUES ('c300000000', '7/7/2016', 'Miles Hester');
INSERT INTO tunings VALUES ('d400000000', '2/14/2020', 'Miles Hester');

CREATE TABLE digital_piano(
    serial_num VARCHAR (10),
    FOREIGN KEY (serial_num) REFERENCES piano,
    PRIMARY KEY (serial_num)
);

INSERT INTO digital_piano VALUES ('a100000000');

CREATE TABLE acoustic_piano(
    serial_num VARCHAR (10),
    is_grand BOOL NOT NULL,
    FOREIGN KEY (serial_num) REFERENCES piano,
    PRIMARY KEY (serial_num)
);

INSERT INTO acoustic_piano VALUES ('b200000000', 'false');
INSERT INTO acoustic_piano VALUES ('c300000000', 'true');
INSERT INTO acoustic_piano VALUES ('d400000000', 'true');

CREATE TABLE purchases(
    piano VARCHAR(10),
    receipt NUMERIC (10,0),
    PRIMARY KEY (piano, receipt),
    FOREIGN KEY (piano) REFERENCES piano,
    FOREIGN KEY (receipt) REFERENCES receipt
);

INSERT INTO purchases VALUES ('b200000000', 0000000000);
INSERT INTO purchases VALUES ('a100000000', 0000000001);
INSERT INTO purchases VALUES ('c300000000', 0000000002);

CREATE INDEX make_model ON piano (make, model);

SELECT MAX(receipt_id) FROM receipt