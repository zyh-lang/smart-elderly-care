-- 服务与工单数据库 db_order 建表脚本 + 服务目录种子数据
-- 应用方式：docker exec -i elderly-mysql mysql -uroot -p123456 db_order < order-work.sql

-- 服务目录表
CREATE TABLE IF NOT EXISTS service_catalog (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    service_name VARCHAR(100) NOT NULL COMMENT '服务名称',
    service_type VARCHAR(50)  NOT NULL COMMENT '服务类型',
    price        DECIMAL(10,2) DEFAULT 0 COMMENT '价格',
    description  VARCHAR(255) DEFAULT NULL COMMENT '服务说明',
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1上架 0下架',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    KEY idx_service_type (service_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务目录表';

-- 订单表
CREATE TABLE IF NOT EXISTS service_order (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    order_no       VARCHAR(40)  NOT NULL COMMENT '订单号',
    family_user_id BIGINT       NOT NULL COMMENT '下单家属用户ID',
    elder_id       BIGINT       DEFAULT NULL COMMENT '老人ID',
    service_id     BIGINT       NOT NULL COMMENT '服务项目ID',
    service_name   VARCHAR(100) DEFAULT NULL COMMENT '服务名称',
    service_type   VARCHAR(50)  DEFAULT NULL COMMENT '服务类型',
    amount         DECIMAL(10,2) DEFAULT NULL COMMENT '金额',
    address        VARCHAR(255) DEFAULT NULL COMMENT '服务地址',
    expect_time    DATETIME     DEFAULT NULL COMMENT '期望服务时间',
    remark         VARCHAR(255) DEFAULT NULL COMMENT '备注',
    status         VARCHAR(20)  NOT NULL DEFAULT 'PENDING_DISPATCH' COMMENT '订单状态',
    create_time    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted        TINYINT      NOT NULL DEFAULT 0,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_family_user (family_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 工单表
CREATE TABLE IF NOT EXISTS work_order (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    work_order_no    VARCHAR(40)  NOT NULL COMMENT '工单号',
    order_id         BIGINT       NOT NULL COMMENT '订单ID',
    service_id       BIGINT       DEFAULT NULL COMMENT '服务项目ID',
    provider_id      BIGINT       DEFAULT NULL COMMENT '服务商ID',
    provider_name    VARCHAR(100) DEFAULT NULL COMMENT '服务商名称',
    provider_user_id BIGINT       DEFAULT NULL COMMENT '服务商登录用户ID',
    status           VARCHAR(20)  NOT NULL DEFAULT 'DISPATCHED' COMMENT '工单状态',
    dispatch_time    DATETIME     DEFAULT NULL COMMENT '派单时间',
    accept_time      DATETIME     DEFAULT NULL COMMENT '开始服务时间',
    finish_time      DATETIME     DEFAULT NULL COMMENT '完成时间',
    create_time      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted          TINYINT      NOT NULL DEFAULT 0,
    UNIQUE KEY uk_work_order_no (work_order_no),
    KEY idx_provider (provider_id),
    KEY idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- 派单记录表
CREATE TABLE IF NOT EXISTS dispatch_record (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    order_id      BIGINT      DEFAULT NULL COMMENT '订单ID',
    work_order_id BIGINT      DEFAULT NULL COMMENT '工单ID',
    provider_id   BIGINT      DEFAULT NULL COMMENT '服务商ID',
    score         DECIMAL(5,3) DEFAULT NULL COMMENT '派单评分',
    create_time   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT     NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='派单记录表';

-- 评价表
CREATE TABLE IF NOT EXISTS evaluation (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    work_order_id BIGINT       NOT NULL COMMENT '工单ID',
    order_id      BIGINT       DEFAULT NULL COMMENT '订单ID',
    user_id       BIGINT       DEFAULT NULL COMMENT '评价人用户ID',
    rating        INT          NOT NULL COMMENT '评分 1-5',
    content       VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    KEY idx_work_order (work_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- 投诉表
CREATE TABLE IF NOT EXISTS complaint (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    work_order_id BIGINT       NOT NULL COMMENT '工单ID',
    order_id      BIGINT       DEFAULT NULL COMMENT '订单ID',
    user_id       BIGINT       DEFAULT NULL COMMENT '投诉人用户ID',
    reason        VARCHAR(500) DEFAULT NULL COMMENT '投诉原因',
    status        VARCHAR(20)  NOT NULL DEFAULT 'PENDING' COMMENT '投诉状态: PENDING/HANDLED',
    create_time   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted       TINYINT      NOT NULL DEFAULT 0,
    KEY idx_work_order (work_order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投诉表';

-- 服务目录种子数据（注意：本脚本只应执行一次，重复执行会产生重复数据）
INSERT INTO service_catalog (service_name, service_type, price, description, status) VALUES
('上门护理', '护理', 100.00, '专业护士上门提供基础护理服务', 1),
('康复理疗', '康复', 150.00, '康复师上门进行康复理疗', 1),
('家政保洁', '保洁', 80.00, '家庭卫生清洁保洁服务', 1),
('助餐做饭', '做饭', 60.00, '上门助餐、做饭服务', 1),
('日常陪护', '陪护', 120.00, '陪同就医、日常看护陪伴', 1);
