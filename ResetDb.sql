DELETE FROM assessments;
DELETE FROM sqlite_sequence WHERE name = 'assessments';
UPDATE sqlite_sequence SET seq = 1 WHERE name = 'assessments';
DELETE FROM courses;
DELETE FROM sqlite_sequence WHERE name = 'courses';
UPDATE sqlite_sequence SET seq = 1 WHERE name = 'courses';
DELETE FROM terms;
DELETE FROM sqlite_sequence WHERE name = 'terms';
UPDATE sqlite_sequence SET seq = 1 WHERE name = 'terms';
DELETE FROM mentors;
DELETE FROM sqlite_sequence WHERE name = 'mentors';
UPDATE sqlite_sequence SET seq = 1 WHERE name = 'mentors';
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (1,'Term 1',16556,16739,'');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (2,'Term 2',16801,16982,'');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (3,'Term 3',16983,17166,'');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (4,'Term 4',17198,17378,'');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (5,'Term 5',17379,17562,'');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (6,'Term 6',17563,17743,'');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (7,'Term 7',17744,17927,'');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (8,'Term 8',17928,18108,'');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (9,'Term 9',18170,18352,'');
INSERT INTO "terms" ("id","name","start","end","notes") VALUES (10,'Term 10',18353,18535,'');