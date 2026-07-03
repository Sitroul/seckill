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

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SeckillService {

    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    public SeckillResult<Void> execute(Long seckillId, Long userId) {
        String stockKey = "seckill:stock:" + seckillId;
        String usersKey = "seckill:users:" + seckillId;

        String luaScript = "local added = redis.call('SADD', KEYS[2], ARGV[1]); "
                + "if added == 0 then return -1 end; "
                + "local stock = redis.call('GET', KEYS[1]); "
                + "if not stock then stock = '0' end; "
                + "if tonumber(stock) > 0 then "
                + "   redis.call('DECR', KEYS[1]); return 1; "
                + "else return 0; end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(luaScript, Long.class);

        List<String> keys = Arrays.asList(stockKey, usersKey);
        Long result = redisTemplate.execute(redisScript, keys, userId.toString());

        if (result == null) {
            log.error("Redis 执行异常: seckillId={}, userId={}", seckillId, userId);
            return SeckillResult.fail("系统繁忙");
        }
        if (result == -1L) {
            log.warn("重复下单: seckillId={}, userId={}", seckillId, userId);
            return SeckillResult.fail("请勿重复下单");
        }
        if (result == 0L) {
            log.warn("库存不足: seckillId={}, userId={}", seckillId, userId);
            return SeckillResult.fail("已售罄");
        }

        log.info("秒杀扣减成功，发送订单消息: seckillId={}, userId={}", seckillId, userId);
        SeckillMessage msg = new SeckillMessage(seckillId, userId);
        rabbitTemplate.convertAndSend(RabbitMQConfig.SECKILL_ORDER_QUEUE, msg);

        return SeckillResult.success("抢购成功");
    }
}