DROP TABLE IF EXISTS `item`;
CREATE TABLE IF NOT EXISTS `item` (
   `id` BIGINT NOT NULL AUTO_INCREMENT,
   `price` INT NOT NULL,
   `stock` INT NOT NULL,
   `name` VARCHAR(50) NOT NULL,
    UNIQUE INDEX `UK_name` (`name`) ,
    PRIMARY KEY (`id`))
;

DROP TABLE IF EXISTS `member`;
CREATE TABLE IF NOT EXISTS  `member` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `address` VARCHAR(50) NOT NULL,
    `name` VARCHAR(30) NOT NULL,
    PRIMARY KEY (`id`))
;

DROP TABLE IF EXISTS `order_item`;
CREATE TABLE IF NOT EXISTS `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `count` INT NOT NULL,
    `total_amount` INT NOT NULL,
    `item_id` BIGINT NOT NULL,
    PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `orders`;
CREATE TABLE IF NOT EXISTS `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `member_id` BIGINT NOT NULL,
    `order_date` DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    `address` VARCHAR(50) NOT NULL,
    `order_status` VARCHAR(20) NOT NULL,
    PRIMARY KEY (`id`));

DROP TABLE IF EXISTS `orders_order_items`;
CREATE TABLE IF NOT EXISTS `orders_order_items` (
    `order_items_id` BIGINT NOT NULL,
    `order_id` BIGINT NOT NULL );
