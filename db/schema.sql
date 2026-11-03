-- =====================================================================
-- 图书管理系统 数据库初始化脚本
-- 数据库: MySQL 8.0+    字符集: utf8mb4
-- 生成方式: tools/gen_sql.py（数据源唯一，勿手工改两个副本）
-- =====================================================================

-- 建表语句（不含 CREATE DATABASE，便于被 init_full.sql 复用）
DROP TABLE IF EXISTS `book`;

CREATE TABLE `book` (
  `id`               BIGINT        NOT NULL AUTO_INCREMENT           COMMENT '主键ID',
  `isbn`             VARCHAR(20)   NOT NULL                          COMMENT 'ISBN 编号',
  `title`            VARCHAR(200)  NOT NULL                          COMMENT '书名',
  `author`           VARCHAR(100)  NOT NULL                          COMMENT '作者',
  `publisher`        VARCHAR(100)  DEFAULT NULL                      COMMENT '出版社',
  `category`         VARCHAR(50)   DEFAULT NULL                      COMMENT '图书分类',
  `publish_date`     DATE          DEFAULT NULL                      COMMENT '出版日期',
  `price`            DECIMAL(10,2) DEFAULT 0.00                      COMMENT '定价（元）',
  `total_copies`     INT           NOT NULL DEFAULT 1                COMMENT '馆藏总数',
  `available_copies` INT           NOT NULL DEFAULT 1                COMMENT '可借数量',
  `location`         VARCHAR(50)   DEFAULT NULL                      COMMENT '馆藏位置',
  `status`           TINYINT       NOT NULL DEFAULT 1                COMMENT '状态：1-在架 0-下架',
  `remark`           VARCHAR(255)  DEFAULT NULL                      COMMENT '备注',
  `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_isbn` (`isbn`),
  KEY `idx_book_title` (`title`),
  KEY `idx_book_category` (`category`),
  KEY `idx_book_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='图书信息表';
