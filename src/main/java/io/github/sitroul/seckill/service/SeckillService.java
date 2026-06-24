package io.github.sitroul.seckill.service;

import io.github.sitroul.seckill.dto.SeckillMessage;
import io.github.sitroul.seckill.dto.SeckillResult;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SeckillService {

    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public SeckillResult<Void> execute(Long seckillId, Long userId) {
        String stockKey = "seckill:stock:" + seckillId;
        Long stock = redisTemplate.opsForValue().decrement(stockKey);

        if (stock == null || stock < 0) {
            redisTemplate.opsForValue().increment(stockKey);
            return SeckillResult.fail("已售罄");
        }

        SeckillMessage msg = new SeckillMessage(seckillId, userId);
        rabbitTemplate.convertAndSend("seckill.order", msg);

        return SeckillResult.success("抢购成功");
    }
}