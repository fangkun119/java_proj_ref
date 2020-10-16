CREATE SCHEMA `ssm` DEFAULT CHARACTER SET utf8;

CREATE TABLE `account`
(
    `id`         int         NOT NULL AUTO_INCREMENT,
    `login_name` varchar(45) NOT NULL,
    `password`   varchar(45) NOT NULL,
    `nick_name`  varchar(45) DEFAULT NULL,
    `age`        int         DEFAULT '0',
    `location`   varchar(45) DEFAULT NULL,
    `role`       varchar(45) DEFAULT 'user',
    PRIMARY KEY (`id`),
    UNIQUE KEY `login_name_UNIQUE` (`login_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 7
  DEFAULT CHARSET = utf8
