CREATE TABLE `users` (
	`id`	bigint	NOT NULL	COMMENT 'auto-increment',
	`ids`	varchar(50)	NOT NULL,
	`password`	varchar(50)	NOT NULL,
	`email`	varchar(100)	NOT NULL,
	`name`	varchar(50)	NOT NULL,
	`status`	enum('ACTIVE', 'INACTIVE', 'DORMANT')	NULL
);
ALTER TABLE `users` ADD CONSTRAINT `PK_USERS` PRIMARY KEY (
	`id`
);
