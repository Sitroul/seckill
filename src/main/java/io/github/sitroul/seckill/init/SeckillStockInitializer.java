package io.github.sitroul.seckill.init;

import io.github.sitroul.seckill.dao.SeckillDao;
import io.github.sitroul.seckill.entity.Seckill;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class SeckillStockInitializer implements CommandLineRunner {

    private final SeckillDao seckillDao;
    private final StringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) {
        log.info("清空 Redis 秒杀缓存...");
        redisTemplate.delete(redisTemplate.keys("seckill:*"));

        log.info("从数据库加载库存...");
        List<Seckill> list = seckillDao.selectAll();
        for (Seckill s : list) {
            redisTemplate.opsForValue().set(
                    "seckill:stock:" + s.getSeckillId(),
                    String.valueOf(s.getNumber())
            );
        }

        log.info("加载完成，{} 个商品", list.size());
    }
}