CREATE TABLE PRODUCT (
    ID INTEGER PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL UNIQUE,
    PRICE FLOAT NOT NULL,
    DESCRIPTION VARCHAR(200),
    STOCK INTEGER NOT NULL,
    IMAGE VARCHAR(200) NOT NULL,
    BRAND_ID INTEGER,
    FOREIGN KEY (BRAND_ID) REFERENCES BRAND (ID)
)

CREATE TABLE BRAND (
    ID INTEGER PRIMARY KEY,
    NAME VARCHAR(50) UNIQUE
)

CREATE TABLE SEASON (
    ID INTEGER PRIMARY KEY,
    NAME VARCHAR(50) NOT NULL UNIQUE,
    START_DATE DATE NOT NULL,
    END_DATE DATE NOT NULL,
    DESCRIPTION VARCHAR(200),
    STATUS CHAR(1) NOT NULL
)

CREATE TABLE SEASON_PRODUCT (
    PRODUCT_ID INTEGER NOT NULL,
    SEASON_ID INTEGER NOT NULL,
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCT (ID),
    FOREIGN KEY (SEASON_ID) REFERENCES SEASON (ID)
)

CREATE TABLE CATEGORY (
    ID INTEGER PRIMARY KEY,
    NAME VARCHAR(100) NOT NULL UNIQUE,
    PARENT_CATEGORY_ID INTEGER,
    FOREIGN KEY (PARENT_CATEGORY_ID) REFERENCES CATEGORY (ID)
)

CREATE TABLE CATEGORY_PRODUCT (
    CATEGORY_ID INTEGER NOT NULL,
    PRODUCT_ID INTEGER NOT NULL ,
    FOREIGN KEY (CATEGORY_ID) REFERENCES CATEGORY (ID),
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCT (ID)
)

CREATE TABLE CUSTOMER (
    ID INTEGER PRIMARY KEY,
    NAMES VARCHAR(100) NOT NULL,
    LAST_NAMES VARCHAR(100) NOT NULL,
    PHONE_NUMBER VARCHAR(8) NOT NULL,
    ADDRESS VARCHAR(200) NOT NULL,
    STATUS CHAR(1) NOT NULL,
    EMAIL VARCHAR(100) NOT NULL,   
    PASSWORD VARCHAR(100) NOT NULL   ----deben ser null
)


CREATE TABLE CREDIT_CARD (
    ID INTEGER PRIMARY KEY,
    TYPE VARCHAR (100) NOT NULL,
    CC_NUMBER VARCHAR(200) NOT NULL,
    CC_DUE_DATE DATE NOT NULL,
    CC_NAME VARCHAR(50) NOT NULL,
    STATUS CHAR(1) NOT NULL,
    CUSTOMER_ID INTEGER NOT NULL,
    FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER(ID)
)


CREATE TABLE "ORDER" (
    ID INTEGER PRIMARY KEY,
    PURCHASE_DATE DATE NOT NULL,
    CUSTOMER_ID INTEGER NOT NULL,
    TOTAL FLOAT NOT NULL,
    STATUS VARCHAR2(30),
    FOREIGN KEY (CUSTOMER_ID) REFERENCES CUSTOMER(ID)
)

CREATE TABLE ORDER_DETAIL (
    ORDER_ID INTEGER NOT NULL,
    PRODUCT_ID INTEGER NOT NULL,
    QTY INTEGER NOT NULL,
    FOREIGN KEY (ORDER_ID) REFERENCES "ORDER" (ID) ON DELETE CASCADE,
    FOREIGN KEY (PRODUCT_ID) REFERENCES PRODUCT (ID)
)



CREATE SEQUENCE PRODUCT_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE BRAND_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEASON_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE CATEGORY_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE CUSTOMER_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE CREDIT_CARD_SEQ START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ORDER_SEQ START WITH 1 INCREMENT BY 1;


-- Trigger para PRODUCT
CREATE OR REPLACE TRIGGER PRODUCT_BI
BEFORE INSERT ON PRODUCT
FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT PRODUCT_SEQ.NEXTVAL INTO :NEW.ID FROM dual;
  END IF;
END;
/

-- Trigger para BRAND
CREATE OR REPLACE TRIGGER BRAND_BI
BEFORE INSERT ON BRAND
FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT BRAND_SEQ.NEXTVAL INTO :NEW.ID FROM dual;
  END IF;
END;
/

-- Trigger para SEASON
CREATE OR REPLACE TRIGGER SEASON_BI
BEFORE INSERT ON SEASON
FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT SEASON_SEQ.NEXTVAL INTO :NEW.ID FROM dual;
  END IF;
END;
/

-- Trigger para CATEGORY
CREATE OR REPLACE TRIGGER CATEGORY_BI
BEFORE INSERT ON CATEGORY
FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT CATEGORY_SEQ.NEXTVAL INTO :NEW.ID FROM dual;
  END IF;
END;
/

-- Trigger para CUSTOMER
CREATE OR REPLACE TRIGGER CUSTOMER_BI
BEFORE INSERT ON CUSTOMER
FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT CUSTOMER_SEQ.NEXTVAL INTO :NEW.ID FROM dual;
  END IF;
END;
/

-- Trigger para CREDIT_CARD
CREATE OR REPLACE TRIGGER CREDIT_CARD_BI
BEFORE INSERT ON CREDIT_CARD
FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT CREDIT_CARD_SEQ.NEXTVAL INTO :NEW.ID FROM dual;
  END IF;
END;
/

-- Trigger para ORDER
CREATE OR REPLACE TRIGGER ORDER_BI
BEFORE INSERT ON "ORDER"
FOR EACH ROW
BEGIN
  IF :NEW.ID IS NULL THEN
    SELECT ORDER_SEQ.NEXTVAL INTO :NEW.ID FROM dual;
  END IF;
END;
/

