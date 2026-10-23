-- 用户中心 db_user 种子数据（服务商、老人、家属绑定，供下单与派单测试）
-- 应用方式：docker exec -i elderly-mysql mysql -uroot -p123456 db_user < user-center-seed.sql

USE db_user;

-- 补充服务商登录账号（服务商需登录后才能操作自己的工单）
INSERT INTO sys_user (username, password, role, real_name, status) VALUES
('provider02', '$2a$10$Hn3h7FdORyOG1GFcYrYOPe2IjCRdsTawvrEqymShbl5WtU29DgLHK', 'PROVIDER', '康乐家政服务商', 1),
('provider03', '$2a$10$Hn3h7FdORyOG1GFcYrYOPe2IjCRdsTawvrEqymShbl5WtU29DgLHK', 'PROVIDER', '顺康医疗陪护中心', 1);

-- 服务商（user_id 关联登录账号；service_types 用于智能派单匹配）
INSERT INTO provider (user_id, provider_name, contact_name, contact_phone, address, service_types, rating, status) VALUES
(3, '安心养老护理服务商', '张师傅', '13800000001', '朝阳社区', '护理,陪护,康复', 4.8, 1),
((SELECT id FROM sys_user WHERE username = 'provider02'), '康乐家政服务公司', '李阿姨', '13800000002', '海淀社区', '家政,保洁,做饭', 4.5, 1),
((SELECT id FROM sys_user WHERE username = 'provider03'), '顺康医疗陪护中心', '王医生', '13800000003', '朝阳社区', '护理,康复,陪护', 4.9, 1);

-- 老人档案（family_user_id 关联家属账号 family01 的 id=4）
INSERT INTO elder (name, gender, age, community, family_user_id) VALUES
('张奶奶', 2, 78, '朝阳社区', 4),
('李爷爷', 1, 82, '海淀社区', 4);

-- 家属-老人绑定
INSERT INTO family_binding (family_user_id, elder_id, relation) VALUES
(4, 1, '子女'),
(4, 2, '子女');
