-- 消息通知数据库 db_notify 建表脚本
-- 应用方式：docker exec -i elderly-mysql mysql -uroot -p123456 db_notify < notify.sql

-- 站内信表
CREATE TABLE IF NOT EXISTS message (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id     BIGINT       NOT NULL COMMENT '接收人用户ID',
    title       VARCHAR(100) NOT NULL COMMENT '标题',
    content     VARCHAR(500) DEFAULT NULL COMMENT '内容',
    msg_type    VARCHAR(20)  NOT NULL DEFAULT 'SYSTEM' COMMENT '消息类型: SYSTEM/ORDER/HEALTH',
    read_flag   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      NOT NULL DEFAULT 0,
    KEY idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内信表';

-- 消息订阅表（无记录默认视为已订阅，status=0 表示退订）
CREATE TABLE IF NOT EXISTS subscribe (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id     BIGINT      NOT NULL COMMENT '用户ID',
    msg_type    VARCHAR(20) NOT NULL COMMENT '消息类型',
    status      TINYINT     NOT NULL DEFAULT 1 COMMENT '1订阅 0退订',
    create_time DATETIME    DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT     NOT NULL DEFAULT 0,
    UNIQUE KEY uk_user_type (user_id, msg_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息订阅表';
