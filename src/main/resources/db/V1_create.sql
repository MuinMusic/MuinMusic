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
    `instrument`VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `instrument` (`instrument` )
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
     `job_seeker_id` BIGINT NULL DEFAULT NULL,
     `career` VARCHAR(255) NULL DEFAULT NULL,
    `title` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`) )
;

select * from job_seeker_profile;
select * from part;
select * from skill;
select * from genre;
select * from job_seeker;

CREATE TABLE `job_seeker_profile_genre` (
     `genre_id` bigint NOT NULL,
     `job_seeker_profile_id` bigint NOT NULL
) ;

CREATE TABLE `job_seeker_profile_part`(
    `part_id` bigint NOT NULL,
    `job_seeker_profile_id` bigint NOT NULL
) ;

CREATE TABLE `job_seeker_profile_skill` (
    `skill_id` bigint NOT NULL,
    `job_seeker_profile_id` bigint NOT NULL
) ;

CREATE TABLE `job_seeker_profile_job_seeker` (
    `job_seeker_id` bigint NOT NULL,
    `job_seeker_profile_id` bigint NOT NULL
) ;
