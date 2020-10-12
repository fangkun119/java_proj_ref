CREATE SCHEMA `ssm` DEFAULT CHARACTER SET utf8 ;

CREATE TABLE `ssm`.`account` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `login_name` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `nick_name` VARCHAR(45) NULL,
  `age` INT NULL DEFAULT 0,
  `location` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `login_name_UNIQUE` (`login_name` ASC) VISIBLE);

