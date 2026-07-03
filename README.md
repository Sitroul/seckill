# 高并发秒杀系统

基于 Spring Boot + Redis + RabbitMQ 的秒杀订单削峰方案，通过 Redis 原子扣减与消息队列异步处理，保障高并发场景下的库存一致性与系统稳定性。

## 技术栈

- Spring Boot 3.x, MyBatis-Plus, Redis, RabbitMQ, MySQL

## 核心设计

- **Redis 预减库存：** Lua 脚本原子执行 `SADD` 防重 + `GET/DECR` 扣库存，避免超卖与重复下单
- **启动刷新：** 应用启动时清空 Redis 缓存，从 MySQL 加载最新库存，避免脏数据
- **异步削峰：** 扣减成功后将订单投递至 RabbitMQ，数据库异步持久化
- **幂等控制：** 消费者先插订单（唯一索引 `uk_seckill_user` 拦截重复消费），再乐观锁减库存（`UPDATE ... WHERE number > 0`），`@Transactional` 保证原子性
- **全局异常处理：** 统一响应封装，接口异常兜底

## 项目结构

```
├── controller/    # 秒杀接口层
├── service/       # 核心业务逻辑（Redis Lua 扣减 + MQ 投递）
├── mq/            # RabbitMQ 消费者（先插订单再减库存，事务包裹）
├── dao/           # 数据访问层（乐观锁减库存）
├── init/          # 启动初始化（MySQL 库存加载至 Redis）
├── dto/           # 数据传输对象
├── config/        # Redis、RabbitMQ 配置
└── exception/     # 全局异常处理
```

## 快速启动

1. 启动 MySQL、Redis、RabbitMQ
2. 创建数据库 `seckill_db`，导入表结构
3. 配置 `application.yml` 或通过环境变量覆盖连接信息
4. 启动应用：

```bash
mvn spring-boot:run
```

## 接口示例

```bash
POST /seckill/{seckillId}/execution?userId={userId}
```

**响应示例：**

```json
{
  "success": true,
  "message": "抢购成功",
  "data": null
}
```