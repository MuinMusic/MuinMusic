DROP TABLE IF EXISTS `employer` ;
CREATE TABLE IF NOT EXISTS `employer` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
    );


DROP TABLE IF EXISTS `job_seeker` ;
CREATE TABLE IF NOT EXISTS `job_seeker` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL,
    `major` VARCHAR(255) NULL DEFAULT NULL,
    `name` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `university` VARCHAR(255) NULL DEFAULT NULL,
    PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS `skill`;
CREATE TABLE IF NOT EXISTS `skill` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `skills` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_skills` (`skills` )
    );


DROP TABLE IF EXISTS `genre` ;
CREATE TABLE IF NOT EXISTS `genre` (
   `id` BIGINT NOT NULL AUTO_INCREMENT,
   `type` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_type` (`type`)
    );


DROP TABLE IF EXISTS `part` ;
CREATE TABLE IF NOT EXISTS `part` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `type` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_type` (`type`)
    );


DROP TABLE IF EXISTS `job_seeker_profile`;
CREATE TABLE IF NOT EXISTS `job_seeker_profile` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `price` INT NOT NULL,
    `job_seeker_id` BIGINT NOT NULL,
    `career` VARCHAR(255) DEFAULT NULL,
    `title` VARCHAR(255) NOT NULL,
    `skill_id` BIGINT NOT NULL,
    `genre_id` BIGINT NOT NULL,
    `part_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_job_seeker_profile_job_seeker_id` FOREIGN KEY (`job_seeker_id`) REFERENCES `job_seeker` (`id`),
    CONSTRAINT `FK_job_seeker_profile_skill_id` FOREIGN KEY (`skill_id`) REFERENCES `skill` (`id`),
    CONSTRAINT `FK_job_seeker_profile_genre_id` FOREIGN KEY (`genre_id`) REFERENCES `genre` (`id`),
    CONSTRAINT `FK_job_seeker_profile_part_id` FOREIGN KEY (`part_id`) REFERENCES `part` (`id`)
    );