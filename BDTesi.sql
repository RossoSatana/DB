DROP database DBTESI;

CREATE DATABASE DBTESI;

USE DBTESI;

CREATE TABLE USER (
	ID VARCHAR (20) PRIMARY KEY,
	PW VARCHAR (20)	NOT NULL,
    LVL INT NOT NULL DEFAULT 1,
    MANA INT DEFAULT 0
);

CREATE TABLE MONSTER (
	DENOMINATION VARCHAR(20) PRIMARY KEY,
    HP INT NOT NULL DEFAULT 1,
    DEF INT NOT NULL DEFAULT 0,
    MDEF INT NOT NULL DEFAULT 0,
    AD INT NOT NULL DEFAULT 0,
    AP INT NOT NULL DEFAULT 0,
    CLASS VARCHAR (20) NOT NULL REFERENCES CLASSES,	/*MELEE, RANGED, MIX? */
    TYPE VARCHAR (20) NOT NULL DEFAULT 'Normal' REFERENCES TYPOLOGY 	/* Normal, Fire, Water, Light, Shadow */
);

CREATE TABLE CLASSES (
    SPEC VARCHAR (20) REFERENCES SPECIALIZATION, /* TANK, HEALER, ADC, APC */
    ATTKRANGE INT NOT NULL,
    
    PRIMARY KEY (SPEC)
);

CREATE TABLE TYPOLOGY (
	TYPE VARCHAR (20) PRIMARY KEY
);

INSERT INTO TYPOLOGY VALUES ('Normal'), ('Fire'), ('Water'), ('Light'), ('Shadow');

CREATE TABLE SPECIALIZATION (
	SPEC VARCHAR (20) PRIMARY KEY,
    HP INT NOT NULL DEFAULT 1,
    DEF INT NOT NULL DEFAULT 1,
    MDEF INT NOT NULL DEFAULT 1,
    AD INT NOT NULL DEFAULT 1,
    AP INT NOT NULL DEFAULT 1
);

INSERT INTO SPECIALIZATION (SPEC, AP) VALUES ('APC', 3);
INSERT INTO SPECIALIZATION (SPEC, AD) VALUES ('ADC', 3);
INSERT INTO SPECIALIZATION (SPEC, DEF, MDEF) VALUES ('Tank', 3, 2);
INSERT INTO SPECIALIZATION (SPEC, AP) VALUES ('Healer', 3);

INSERT INTO CLASSES VALUES ('APC', 3), ('ADC', 3), ('Tank', 1), ('Healer', 3);

CREATE TABLE SUPPLIES (
	NAME VARCHAR (20) PRIMARY KEY,
	VALUE INT NOT NULL DEFAULT 1,
    HP INT NOT NULL DEFAULT 0,
    STATUS VARCHAR (20) DEFAULT null
);

CREATE TABLE SUPPLIES_OWNED (
	NAME VARCHAR (20) NOT NULL REFERENCES SUPPLIES,
	QUANTITY INT NOT NULL DEFAULT 1,
    ID_OWNER VARCHAR (20) NOT NULL REFERENCES USER(ID),
    
    PRIMARY KEY (NAME, ID_OWNER)
);

CREATE TABLE WEARABLE (
	NAME VARCHAR (20) PRIMARY KEY,
    TYPE VARCHAR (20) NOT NULL,	/* CAP, CHEST, LEG, SHOES, GLOVES, ACCESSORIES */
	HP INT NOT NULL DEFAULT 0,
    DEF INT NOT NULL DEFAULT 0,
    MDEF INT NOT NULL DEFAULT 0,
    AD INT NOT NULL DEFAULT 0, 
    AP INT NOT NULL DEFAULT 0
);

CREATE TABLE WERABLE_OWNED (
	NAME VARCHAR (20) NOT NULL REFERENCES WEARABLE,
	QUANTITY INT NOT NULL DEFAULT 1,
    ID_OWNER VARCHAR (20) NOT NULL REFERENCES USER(ID),    
    
    PRIMARY KEY (NAME, ID_OWNER)
);

CREATE TABLE EQUIPPED (
	COD_M VARCHAR(20) NOT NULL REFERENCES MONSTER_OWNED,
	NAME VARCHAR (20) NOT NULL REFERENCES WERABLE_OWNED,
    PRIMARY KEY (COD_M, NAME)
);

CREATE TABLE EQUIPMENT(
	COD_M INT REFERENCES MONSTER_OWNED,
    COD_O INT REFERENCES OBJECT_OWNED,
    
    PRIMARY KEY (COD_M, COD_O)
);

CREATE TABLE ABILITY (
	NAME VARCHAR (20) PRIMARY KEY,
    LVL INT NOT NULL DEFAULT 1,
    TYPE VARCHAR (20) NOT NULL DEFAULT 'Normal' REFERENCES TYPOLOGY,
    CLASS VARCHAR (20) REFERENCES CLASSES,
	ATTK_RANGE INT NOT NULL DEFAULT 1,
    STATUS VARCHAR (20) DEFAULT null,		/* Frozen, Poisoned */
	STATUS_DURATION INT DEFAULT null,
    AD INT NOT NULL DEFAULT 0,
    AP INT NOT NULL DEFAULT 0,
    HEAL INT NOT NULL DEFAULT 0,
    CD INT NOT NULL DEFAULT 0,      /* RICARICA ABILITÀ, MISURATO IN TURNI */
    CT INT NOT NULL DEFAULT 0		/* CAST TIME */
);

INSERT INTO ABILITY (NAME, ATTK_RANGE, AD) VALUES ('Arrow', 3, 3); 
INSERT INTO ABILITY (NAME, TYPE, STATUS, ATTK_RANGE, AD, AP) VALUES ('Frost arrow', 'Water', 'Frozen', 3, 1, 10); 
INSERT INTO ABILITY (NAME, TYPE, ATTK_RANGE, HEAL) VALUES ('Blessing', 'Light', 3, 10); 


CREATE TABLE MONSTER_OWNED (
	COD_M INT PRIMARY KEY AUTO_INCREMENT,
    DENOMINATION VARCHAR(20) NOT NULL REFERENCES MONSTER,
    NAME VARCHAR (20),
	LVL INT NOT NULL DEFAULT 1,
	EXP INT NOT NULL DEFAULT 0,
    ID_OWNER VARCHAR(20) REFERENCES USER
);

CREATE TABLE MONSTER_ABILITY (
	COD_M INT REFERENCES MONSTER_OWNED,
	NAME VARCHAR (20) REFERENCES ABILITY,
    PRIMARY KEY (COD_M, NAME)
);

CREATE TABLE TEAM (						/* Da 0 a 9 istanze per ogni user (vincolo java) */
	ID_USER VARCHAR(20) NOT NULL,
	COD_M INT REFERENCES MONSTER_OWNED,		/* DA VINCOLARE (SCELTA SOLO TRA MOSTRI POSSEDUTI) IN JAVA*/
    
    FOREIGN KEY (ID_USER) REFERENCES USER(ID),
	PRIMARY KEY (COD_M),
    UNIQUE (ID_USER, COD_M)
);

CREATE TABLE MATCHMAKING(
	ID VARCHAR(20) NOT NULL REFERENCES USER(ID),
    LVL INT NOT NULL REFERENCES USER(LVL),
	PRIORITY INT AUTO_INCREMENT PRIMARY KEY,
    
    UNIQUE (ID)
);

CREATE TABLE GAME(
	ID1 VARCHAR (20) NOT NULL REFERENCES USER(ID),
    ID2 VARCHAR (20) NOT NULL REFERENCES USER(ID),
    PRIMARY KEY (ID1, ID2),
    UNIQUE (ID1),
    UNIQUE (ID2)
);

CREATE TABLE MONSTER_FIGHTING (			/* PERDO LA COGNIZIONE DI QUANTI MOSTRI PUÒ USARE UN UTENTE */
	COD_M INT REFERENCES TEAM,
    POS INT NOT NULL,
	STATUS VARCHAR(20),
    HP INT NOT NULL,
    DEF INT NOT NULL,
    MDEF INT NOT NULL,
    AD INT NOT NULL,
    AP INT NOT NULL,
    
    PRIMARY KEY (COD_M),
    UNIQUE (POS, COD_M)
);

CREATE TABLE M_ACTION (
	COD_M INT REFERENCES MONSTER_FIGHTING,
    COD_MA INT REFERENCES MONSTER_FIGHTING
);


/* INSERIMENTO DATI DI PROVA 

INSERT INTO USER (ID, PW) VALUES ('KikiValium', '123');
INSERT INTO USER (ID, PW, mana) VALUES ('FilloGrasso', 'muffin', 3);
INSERT INTO USER (ID, PW, mana) VALUES ('GiantFillo27', 'muffin', 3);

INSERT INTO MONSTER (DENOMINATION, CLASS) VALUES ('Animal', 'Melee'), ('Ameba', 'Healer');

INSERT INTO MONSTER_OWNED (DENOMINATION, NAME, ID_OWNER) VALUES ('Animal', 'Summer', 'KikiValium');
INSERT INTO MONSTER_OWNED (DENOMINATION, NAME, ID_OWNER, LVL) VALUES ('Ameba', 'Fil', 'KikiValium', 27);

INSERT INTO MONSTER_OWNED (DENOMINATION, NAME, ID_OWNER, ExP) VALUES ('Animal', 'Pene', 'FilloGrasso', 10000);
INSERT INTO MONSTER_OWNED (DENOMINATION, NAME, ID_OWNER) VALUES ('Ameba', 'Yogurt', 'FilloGrasso');

INSERT INTO MONSTER_OWNED (DENOMINATION, NAME, ID_OWNER, LVL) VALUES ('Animal', 'Pene27', 'GiantFillo27', 101);

INSERT INTO MONSTER_ABILITY VALUES (1, 'Frost arrow');
INSERT INTO MONSTER_ABILITY VALUES (1, 'Arrow');
INSERT INTO MONSTER_ABILITY VALUES (3, 'Frost arrow');
INSERT INTO MONSTER_ABILITY VALUES (2, 'Blessing');

INSERT INTO MATCHMAKING (ID, LVL) VALUES ('FilloGrasso', 1);
INSERT INTO MATCHMAKING (ID, LVL) VALUES ('GiantFillo27', 27);

INSERT INTO GAME VALUES ('FilloGrasso', 'GiantFillo27');

/* QUERY 

INSERT INTO TEAM (COD_M, ID_USER)
SELECT COD_M, ID_OWNER
FROM MONSTER_OWNED
    WHERE ID_OWNER = 'FilloGrasso'
    AND NAME = 'Pene';
    
INSERT INTO TEAM (COD_M, ID_USER)
SELECT COD_M, ID_OWNER
FROM MONSTER_OWNED mo
    WHERE mo.ID_OWNER = 'GiantFillo27'
    AND mo.NAME = 'Pene27';

/* In java: fare una query che richiede il cod_m di ogni mostro, e inviare direttamente cod_m 

INSERT INTO MONSTER_FIGHTING (COD_M, POS) VALUES (3, 2);
INSERT INTO MONSTER_FIGHTING (COD_M, POS) VALUES (5, 9);

/* INSERT INTO MONSTER_FIGHTING (COD_M, POS)
SELECT COD_M
FROM TEAM t, USER_FIGHTING uf 
	WHERE t.ID_USER = uf.ID
    GROUP BY COD_M; /* INSERIMENTO AUTOMATIZZATO 

SELECT *
FROM MONSTER_OWNED;

SELECT *
FROM TEAM
WHERE ID_USER = 'KikiValium';

SELECT ID
FROM USER FIGHTING;

SELECT *
FROM MONSTER_FIGHTING mf, MONSTER_OWNED mo
WHERE mf.COD_M=mo.COD_M;

SELECT *
FROM MATCHMAKING 
ORDER BY PRIORITY;

SELECT *
FROM GAME
WHERE ID1 OR ID2 = 'FilloGrasso'
AND ID1 OR ID2 = 'GiantFillo27';


*/