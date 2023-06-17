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

-- EnterPromotion --
CREATE TABLE IF NOT EXISTS entry_promotion (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '고유 식별자',
    code varchar(30) NOT NULL COMMENT '프로모션 코드',
    title varchar(50) NOT NULL COMMENT '프로모션 제목',
    start_at datetime NOT NULL COMMENT '프로모션 시작 일시',
    end_at datetime NOT NULL COMMENT '프로모션 종료 일시',
    created_at datetime NOT NULL COMMENT '프로모션 등록 일시'
);

CREATE UNIQUE INDEX uk_code ON entry_promotion(code);
CREATE INDEX idx_startat ON entry_promotion(start_at);
CREATE INDEX idx_createdat ON entry_promotion(created_at);

CREATE TABLE IF NOT EXISTS entry (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '고유 식별자',
    member_id BIGINT NOT NULL COMMENT '회원 고유 식별자',
    promotion_code varchar(30) NOT NULL COMMENT '프로모션 코드',
    entered_at datetime NOT NULL COMMENT '응모 일시'
);

CREATE UNIQUE INDEX uk_memberid_promotioncode ON entry(member_id, promotion_code);
CREATE INDEX idx_promotioncode ON entry(promotion_code

