# 基于 Spring Cloud Alibaba 的社区智慧养老服务系统

本科毕业设计项目：面向社区居家养老场景的微服务系统，覆盖服务下单与智能派单、工单流转与评价、健康档案管理、消息推送、服务统计与报表五大核心功能。

## 技术栈

- **后端**：Spring Boot 3.2.4 / Spring Cloud 2023.0.1 / Spring Cloud Alibaba 2023.0.1.0，JDK 17
- **微服务组件**：Nacos（注册+配置）、Spring Cloud Gateway、Sentinel、OpenFeign
- **数据层**：MyBatis-Plus + MySQL 8.0 + Redis 7
- **前端**：Vue3 + Element Plus 管理端；原生微信小程序家属端
- **部署**：VMware + CentOS 7 虚拟机，Docker 部署中间件

## 模块结构

| 模块 | 说明 |
|------|------|
| gateway | 网关（路由/JWT 鉴权/Sentinel 限流），8080 |
| user-center | 用户中心（登录注册/RBAC/老人/服务商），8081 |
| order-work | 服务与工单（下单/智能派单/状态机/评价投诉/统计源），8082 |
| health-record | 健康档案（档案/指标/用药），8083 |
| notify | 消息通知（站内信/订阅），8084 |
| statistics | 统计报表（Feign 聚合 + Redis 缓存），8085 |
| common/* | 公共模块（core/web/redis/security） |
| frontend/admin-web | Vue3 管理端 |
| frontend/family-miniapp | 微信小程序家属端 |
| sql/ | 建库建表与种子脚本 |
| infra/ | Docker Compose 编排（Nacos/MySQL/Redis） |
| docs/ | 需求/概要/详细设计、测试报告、用户手册、部署说明 |

## 快速启动

1. 虚拟机部署中间件：见 [docs/部署说明.md](docs/部署说明.md)；
2. 初始化数据库：执行 `sql/` 下脚本；
3. 依次启动 6 个后端服务（IDEA 或 `mvn spring-boot:run`）；
4. 管理端：`cd frontend/admin-web && npm install && npm run dev` → http://localhost:5173（admin/123456）；
5. 小程序：微信开发者工具导入 `frontend/family-miniapp`（family01/123456）。

## 测试

`mvn -pl order-work -am test`（智能派单、工单状态机、JWT 单元测试）。

## 文档

- 需求规格说明书 / 概要设计说明书 / 详细设计说明书 / 测试报告 / 用户使用手册 / 部署说明（docs/ 目录）
- 接口文档：各服务启动后访问 `http://localhost:{端口}/doc.html`（Knife4j）
