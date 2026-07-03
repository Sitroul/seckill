package io.github.sitroul.seckill.mq;

import io.github.sitroul.seckill.dao.SeckillDao;
import io.github.sitroul.seckill.dto.SeckillMessage;
import io.github.sitroul.seckill.entity.SeckillOrder;
import io.github.sitroul.seckill.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@AllArgsConstructor
public class SeckillConsumer {

    private final OrderService orderService;
    private final SeckillDao seckillDao;

    @RabbitListener(queues = "seckill.order")
    @Transactional(rollbackFor = Exception.class)
    public void handleOrder(SeckillMessage message) {
        log.info("收到订单消息: {}", message);

        // 1. 先插订单：唯一索引 (seckill_id, user_id) 自动防 MQ 重复消费
        SeckillOrder order = new SeckillOrder();
        order.setSeckillId(message.getSeckillId());
        order.setUserId(message.getUserId());
        order.setState((short) 0);

        try {
            orderService.save(order);
        } catch (DuplicateKeyException e) {
            log.warn("重复消费，已处理过，忽略: {}", message);
            return;
        }

        // 2. 再减数据库库存
        // Redis 已原子扣减并保证库存够，若此处库存不足说明系统数据不一致，直接抛异常
        int affected = seckillDao.reduceStock(message.getSeckillId());
        if (affected == 0) {
            log.error("数据不一致: Redis已扣减但数据库库存不足, seckillId={}", message.getSeckillId());
            throw new IllegalStateException("库存数据不一致: seckillId=" + message.getSeckillId());
        }

        log.info("订单处理成功: seckillId={}, userId={}",
                message.getSeckillId(), message.getUserId());
    }
}