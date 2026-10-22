-- 用户中心数据库 db_user 建表脚本 + 种子数据
-- 应用方式：docker exec -i elderly-mysql mysql -uroot -p123456 db_user < user-center.sql

-- 系统用户表（四类角色同表，role 字段区分：ADMIN/STAFF/PROVIDER/FAMILY）
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username    VARCHAR(50)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码(BCrypt)',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    role        VARCHAR(20)  NOT NULL COMMENT '角色: ADMIN/STAFF/PROVIDER/FAMILY',
    real_name   VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0禁用',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删 1已删',
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 老人档案表
CREATE TABLE IF NOT EXISTS elder (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name              VARCHAR(50)  NOT NULL COMMENT '老人姓名',
    gender            TINYINT      DEFAULT 1 COMMENT '性别: 1男 2女',
    age               INT          DEFAULT NULL COMMENT '年龄',
    id_card           VARCHAR(30)  DEFAULT NULL COMMENT '身份证号',
    phone             VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    address           VARCHAR(255) DEFAULT NULL COMMENT '住址',
    community         VARCHAR(100) DEFAULT NULL COMMENT '所属社区',
    emergency_contact VARCHAR(50)  DEFAULT NULL COMMENT '紧急联系人',
    emergency_phone   VARCHAR(20)  DEFAULT NULL COMMENT '紧急联系电话',
    family_user_id    BIGINT       DEFAULT NULL COMMENT '绑定家属用户ID',
    create_time       DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time       DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted           TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_family_user (family_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='老人档案表';

-- 家属-老人绑定表
CREATE TABLE IF NOT EXISTS family_binding (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    family_user_id BIGINT      NOT NULL COMMENT '家属用户ID',
    elder_id       BIGINT      NOT NULL COMMENT '老人ID',
    relation       VARCHAR(20) DEFAULT NULL COMMENT '关系: 子女/配偶等',
    create_time    DATETIME    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted        TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    UNIQUE KEY uk_family_elder (family_user_id, elder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家属-老人绑定表';

-- 服务商表
CREATE TABLE IF NOT EXISTS provider (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    user_id       BIGINT        DEFAULT NULL COMMENT '关联登录用户ID',
    provider_name VARCHAR(100)  NOT NULL COMMENT '服务商名称',
    contact_name  VARCHAR(50)   DEFAULT NULL COMMENT '联系人',
    contact_phone VARCHAR(20)   DEFAULT NULL COMMENT '联系电话',
    address       VARCHAR(255)  DEFAULT NULL COMMENT '地址',
    service_types VARCHAR(255)  DEFAULT NULL COMMENT '可提供服务类型(逗号分隔)',
    rating        DECIMAL(2,1)  DEFAULT 5.0 COMMENT '评分(1-5)',
    status        TINYINT       NOT NULL DEFAULT 1 COMMENT '状态: 1正常 0停用',
    create_time   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    KEY idx_provider_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='服务商表';

-- 种子数据（密码均为 123456 的 BCrypt 哈希）
INSERT INTO sys_user (username, password, role, real_name, status) VALUES
('admin', '$2a$10$Hn3h7FdORyOG1GFcYrYOPe2IjCRdsTawvrEqymShbl5WtU29DgLHK', 'ADMIN', '系统管理员', 1),
('staff01', '$2a$10$Hn3h7FdORyOG1GFcYrYOPe2IjCRdsTawvrEqymShbl5WtU29DgLHK', 'STAFF', '社区工作人员', 1),
('provider01', '$2a$10$Hn3h7FdORyOG1GFcYrYOPe2IjCRdsTawvrEqymShbl5WtU29DgLHK', 'PROVIDER', '服务商', 1),
('family01', '$2a$10$Hn3h7FdORyOG1GFcYrYOPe2IjCRdsTawvrEqymShbl5WtU29DgLHK', 'FAMILY', '家属', 1);
