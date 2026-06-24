package io.github.sitroul.seckill.service;

import io.github.sitroul.seckill.config.RabbitMQConfig;
import io.github.sitroul.seckill.dto.SeckillMessage;
import io.github.sitroul.seckill.dto.SeckillResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@AllArgsConstructor
public class SeckillService {

    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public SeckillResult<Void> execute(Long seckillId, Long userId) {
        String stockKey = "seckill:stock:" + seckillId;

        String luaScript = "local stock = redis.call('GET', KEYS[1]); "
                + "if not stock then stock = '0' end; "
                + "if tonumber(stock) > 0 then "
                + "   redis.call('DECR', KEYS[1]); return 1; "
                + "else return 0; end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);

        Long result = redisTemplate.execute(redisScript, Collections.singletonList(stockKey));

        if (result == null || result != 1) {
            log.warn("秒杀抢购失败(库存不足): seckillId={}, userId={}", seckillId, userId);
            return SeckillResult.fail("已售罄");
        }

        log.info("秒杀扣减成功，发送订单消息: seckillId={}, userId={}", seckillId, userId);

        SeckillMessage msg = new SeckillMessage(seckillId, userId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.SECKILL_ORDER_QUEUE, msg);

        return SeckillResult.success("抢购成功");
    }
}
