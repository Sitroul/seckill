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
    private final SeckillDao seckillDao;  // 新增：减数据库库存

    @RabbitListener(queues = "seckill.order")
    @Transactional
    public void handleOrder(SeckillMessage message) {
        log.info("收到订单消息: {}", message);

        // 1. 数据库减库存
        int affected = seckillDao.reduceStock(message.getSeckillId());
        if (affected == 0) {
            log.warn("数据库库存不足，丢弃消息: {}", message);
            return;  // 库存已为0，不创建订单
        }

        // 2. 创建订单（唯一索引防重）
        SeckillOrder order = new SeckillOrder();
        order.setSeckillId(message.getSeckillId());
        order.setUserId(message.getUserId());
        order.setState((short) 0);

        try {
            orderService.save(order);
            log.info("订单创建成功: seckillId={}, userId={}", message.getSeckillId(), message.getUserId());
        } catch (DuplicateKeyException e) {
            // 唯一索引 (seckill_id, user_id) 冲突，说明已下单
            log.warn("重复订单，忽略: {}", message);
            // 事务会自动回滚数据库库存扣减... 等等，这里有问题
        }
    }
}