CREATE TABLE User(
	id            INTEGER  PRIMARY KEY AUTOINCREMENT,
	username      TEXT     NOT NULL UNIQUE,
	password      TEXT     NOT NULL,
	access_level  TEXT CHECK(access_level IN ('adm', 'man', 'op'))  NOT NULL
);

CREATE TABLE Product(
	id            INTEGER  PRIMARY KEY AUTOINCREMENT,
	name          TEXT     NOT NULL UNIQUE,
	category_id   INTEGER,
	unit          TEXT CHECK(unit IN ('each', 'kg'))  NOT NULL DEFAULT 'each',
	unit_price    INTEGER  NOT NULL,
	units_stored  INTEGER  NOT NULL DEFAULT 0,
	units_min     INTEGER  NOT NULL DEFAULT 0,
	units_sold    INTEGER  NOT NULL DEFAULT 0
);

CREATE TABLE ProductOrder(
	id            INTEGER  PRIMARY KEY AUTOINCREMENT,
	client_id     INTEGER,
	value         INTEGER  NOT NULL,
	is_paid       BOOLEAN  NOT NULL DEFAULT 0,
	datetime      TEXT     NOT NULL
);

CREATE TABLE ProductTransaction(
	id            INTEGER  PRIMARY KEY AUTOINCREMENT,
	product_id    INTEGER  NOT NULL,
	title_id      INTEGER,
	unit_price    INTEGER  NOT NULL,
	units         INTEGER  NOT NULL
);

CREATE TABLE Client(
	id            INTEGER  PRIMARY KEY AUTOINCREMENT,
	name          TEXT     NOT NULL UNIQUE,
	phone_number  TEXT     NOT NULL UNIQUE
);

CREATE TABLE Occurrence(
	id            INTEGER  PRIMARY KEY AUTOINCREMENT,
	user_id       INTEGER,
	type          TEXT CHECK(type IN ('int', 'erp'))  NOT NULL,
	text          TEXT     NOT NULL,
	is_solved     BOOLEAN  NOT NULL DEFAULT 0
);
