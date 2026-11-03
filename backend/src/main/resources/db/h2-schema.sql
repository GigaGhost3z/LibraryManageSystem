-- =====================================================================
-- H2 兼容建表脚本（仅供 Spring Boot 的 h2 profile 使用）
-- 由 tools/gen_sql.py 生成；与 db/schema.sql 结构一致，去掉了 MySQL 专有子句
-- =====================================================================
DROP TABLE IF EXISTS book;

CREATE TABLE book (
  id               BIGINT        NOT NULL AUTO_INCREMENT,
  isbn             VARCHAR(20)   NOT NULL,
  title            VARCHAR(200)  NOT NULL,
  author           VARCHAR(100)  NOT NULL,
  publisher        VARCHAR(100)  DEFAULT NULL,
  category         VARCHAR(50)   DEFAULT NULL,
  publish_date     DATE          DEFAULT NULL,
  price            DECIMAL(10,2) DEFAULT 0.00,
  total_copies     INT           NOT NULL DEFAULT 1,
  available_copies INT           NOT NULL DEFAULT 1,
  location         VARCHAR(50)   DEFAULT NULL,
  status           TINYINT       NOT NULL DEFAULT 1,
  remark           VARCHAR(255)  DEFAULT NULL,
  create_time      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uk_book_isbn UNIQUE (isbn)
);
