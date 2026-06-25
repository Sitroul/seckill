# 高并发秒杀系统

基于 Spring Boot + Redis + RabbitMQ 的秒杀订单削峰方案，通过 Redis 原子扣减与消息队列异步处理，保障高并发场景下的库存一致性与系统稳定性。

## 技术栈

- Spring Boot, MyBatis-Plus, Redis, RabbitMQ, JWT

## 核心设计

- **Redis 预减库存：** Lua 脚本保证原子性，防止超卖
- **异步削峰：** 订单请求入 RabbitMQ 队列，缓解数据库瞬时压力
- **幂等控制：** Redis Set 去重 + 数据库唯一索引，防止重复下单
- **全局异常处理：** 统一响应封装，接口异常兜底

## 项目结构

```
├── controller/    # 秒杀接口层
├── service/       # 核心业务逻辑（Redis 扣减 + MQ 投递）
├── mq/            # RabbitMQ 消费者（数据库持久化）
├── dao/           # 数据访问层
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
