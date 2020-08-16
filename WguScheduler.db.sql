BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "android_metadata" (
	"locale"	TEXT
);
CREATE TABLE IF NOT EXISTS "terms" (
	"id"	INTEGER,
	"name"	TEXT,
	"start"	INTEGER,
	"end"	INTEGER,
	"notes"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "mentors" (
	"id"	INTEGER,
	"name"	TEXT,
	"phoneNumbers"	TEXT,
	"emailAddresses"	TEXT,
	"notes"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "courses" (
	"id"	INTEGER,
	"termId"	INTEGER NOT NULL,
	"mentorId"	INTEGER NOT NULL,
	"number"	TEXT,
	"title"	TEXT,
	"expectedStart"	INTEGER,
	"actualStart"	INTEGER,
	"expectedEnd"	INTEGER,
	"actualEnd"	INTEGER,
	"status"	INTEGER,
	"notes"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "assessments" (
	"id"	INTEGER,
	"courseId"	INTEGER NOT NULL,
	"code"	TEXT,
	"title"	TEXT,
	"status"	INTEGER,
	"goalDate"	INTEGER,
	"evaluationDate"	INTEGER,
	"performanceAssessment"	INTEGER NOT NULL,
	"notes"	TEXT,
	PRIMARY KEY("id" AUTOINCREMENT)
);
CREATE TABLE IF NOT EXISTS "room_master_table" (
	"id"	INTEGER,
	"identity_hash"	TEXT,
	PRIMARY KEY("id")
);
INSERT INTO "android_metadata" ("locale") VALUES ('en_US');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (1,'Term 1',18515,18706,'Line 1
Line 2');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (2,'',NULL,NULL,'');
INSERT INTO "room_master_table" ("id","identity_hash") VALUES (42,'1d0bc6467487f184360365d93f40adcd');
CREATE UNIQUE INDEX IF NOT EXISTS "IDX_TERM_NAME" ON "terms" (
	"name"
);
CREATE UNIQUE INDEX IF NOT EXISTS "IDX_MENTOR_NAME" ON "mentors" (
	"name"
);
CREATE INDEX IF NOT EXISTS "IDX_COURSE_TERM" ON "courses" (
	"termId"
);
CREATE INDEX IF NOT EXISTS "IDX_COURSE_MENTOR" ON "courses" (
	"mentorId"
);
CREATE UNIQUE INDEX IF NOT EXISTS "IDX_COURSE_NUMBER" ON "courses" (
	"number"
);
CREATE INDEX IF NOT EXISTS "IDX_ASSESSMENT_COURSE" ON "assessments" (
	"courseId"
);
CREATE UNIQUE INDEX IF NOT EXISTS "IDX_ASSESSMENT_CODE" ON "assessments" (
	"code"
);
COMMIT;
