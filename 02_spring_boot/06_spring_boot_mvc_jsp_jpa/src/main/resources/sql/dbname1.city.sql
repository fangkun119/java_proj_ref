CREATE SCHEMA `dbname1` ;
CREATE TABLE `dbname1`.`city` (
  `id` INT NOT NULL,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));
INSERT INTO dbname1.city VALUES (10, 'BeiJing')
SELECT * FROM dbname1.city LIMIT 0, 1000
