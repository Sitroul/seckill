# 高并发秒杀系统

基于 Spring Boot + Redis + RabbitMQ 的秒杀订单削峰架构。

## 技术栈

- Spring Boot 4.1.0
- Redis（预减库存 + Lua 原子性）
- RabbitMQ（异步订单处理）
- MyBatis-Plus
- MySQL

## 核心设计

1. **Redis 预减库存**：通过 Lua 脚本保证原子性，防止超卖
2. **MQ 异步削峰**：订单请求入队，缓解数据库瞬时压力
3. **接口限流**：基于令牌桶/计数器限制并发请求

## 快速启动

1. 配置环境变量或 `application.yml` 中的数据库/Redis/RabbitMQ 连接
2. 初始化数据库：`docs/schema.sql`
3. 启动应用：`mvn spring-boot:run`

## 接口

| 接口 | 方法 | 说明 |
|-----|------|------|
| /seckill/{id}/execution | POST | 执行秒杀 |

