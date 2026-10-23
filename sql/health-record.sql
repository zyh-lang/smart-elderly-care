-- 健康档案数据库 db_health 建表脚本
-- 应用方式：docker exec -i elderly-mysql mysql -uroot -p123456 db_health < health-record.sql

-- 健康档案表（每个老人一条）
CREATE TABLE IF NOT EXISTS health_record (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    elder_id        BIGINT       NOT NULL COMMENT '老人ID',
    height          DECIMAL(5,2) DEFAULT NULL COMMENT '身高(cm)',
    weight          DECIMAL(5,2) DEFAULT NULL COMMENT '体重(kg)',
    blood_type      VARCHAR(10)  DEFAULT NULL COMMENT '血型',
    allergy_history VARCHAR(255) DEFAULT NULL COMMENT '过敏史',
    medical_history VARCHAR(500) DEFAULT NULL COMMENT '既往病史',
    family_history  VARCHAR(500) DEFAULT NULL COMMENT '家族病史',
    living_habits   VARCHAR(255) DEFAULT NULL COMMENT '生活习惯',
    create_time     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted         TINYINT      NOT NULL DEFAULT 0,
    UNIQUE KEY uk_elder (elder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康档案表';

-- 健康指标表（时序数据：血压/血糖/心率/体温/体重）
CREATE TABLE IF NOT EXISTS health_metric (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    elder_id     BIGINT       NOT NULL COMMENT '老人ID',
    metric_type  VARCHAR(30)  NOT NULL COMMENT '指标类型: BLOOD_PRESSURE/BLOOD_SUGAR/HEART_RATE/TEMPERATURE/WEIGHT',
    metric_value VARCHAR(50)  NOT NULL COMMENT '指标值(如 120/80 或 5.6)',
    remark       VARCHAR(255) DEFAULT NULL COMMENT '备注',
    measure_time DATETIME     DEFAULT NULL COMMENT '测量时间',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted      TINYINT      NOT NULL DEFAULT 0,
    KEY idx_elder_type (elder_id, metric_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='健康指标表';

-- 用药记录表
CREATE TABLE IF NOT EXISTS medication (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    elder_id    BIGINT       NOT NULL COMMENT '老人ID',
    drug_name   VARCHAR(100) NOT NULL COMMENT '药品名称',
    dosage      VARCHAR(50)  DEFAULT NULL COMMENT '剂量',
    frequency   VARCHAR(50)  DEFAULT NULL COMMENT '频次(如 每日两次)',
    remind_time VARCHAR(50)  DEFAULT NULL COMMENT '提醒时间(如 08:00,20:00)',
    start_date  DATE         DEFAULT NULL COMMENT '开始日期',
    end_date    DATE         DEFAULT NULL COMMENT '结束日期',
    remark      VARCHAR(255) DEFAULT NULL COMMENT '备注',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1服用中 0已停用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    KEY idx_elder (elder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用药记录表';
