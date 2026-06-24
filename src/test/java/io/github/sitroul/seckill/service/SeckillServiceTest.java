package io.github.sitroul.seckill.service;

import io.github.sitroul.seckill.config.RabbitMQConfig;
import io.github.sitroul.seckill.dto.SeckillMessage;
import io.github.sitroul.seckill.dto.SeckillResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SeckillServiceTest {

    private SeckillService seckillService;
    private StringRedisTemplate redisTemplate;
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        rabbitTemplate = mock(RabbitTemplate.class);
        seckillService = new SeckillService(redisTemplate, rabbitTemplate);
    }

    @Test
    void testExecuteSuccess() {
        Long seckillId = 1L;
        Long userId = 1001L;

        when(redisTemplate.execute(any(DefaultRedisScript.class), eq(Collections.singletonList("seckill:stock:1"))))
                .thenReturn(1L);

        SeckillResult<Void> result = seckillService.execute(seckillId, userId);

        assertTrue(result.isSuccess());
        assertEquals("抢购成功", result.getMessage());

        ArgumentCaptor<String> routingCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SeckillMessage> messageCaptor = ArgumentCaptor.forClass(SeckillMessage.class);
        verify(rabbitTemplate).convertAndSend(routingCaptor.capture(), messageCaptor.capture());
        assertEquals(RabbitMQConfig.SECKILL_ORDER_QUEUE, routingCaptor.getValue());
        assertEquals(seckillId, messageCaptor.getValue().getSeckillId());
        assertEquals(userId, messageCaptor.getValue().getUserId());
    }

    @Test
    void testExecuteStockInsufficient() {
        Long seckillId = 2L;
        Long userId = 2002L;

        when(redisTemplate.execute(any(DefaultRedisScript.class), eq(Collections.singletonList("seckill:stock:2"))))
                .thenReturn(0L);

        SeckillResult<Void> result = seckillService.execute(seckillId, userId);

        assertFalse(result.isSuccess());
        assertEquals("已售罄", result.getMessage());

        verify(rabbitTemplate, never()).convertAndSend(anyString(), any());
    }

    @Test
    void testExecuteRedisReturnsNull() {
        Long seckillId = 3L;
        Long userId = 3003L;

        when(redisTemplate.execute(any(DefaultRedisScript.class), eq(Collections.singletonList("seckill:stock:3"))))
                .thenReturn(null);

        SeckillResult<Void> result = seckillService.execute(seckillId, userId);

        assertFalse(result.isSuccess());
        assertEquals("已售罄", result.getMessage());

        verify(rabbitTemplate, never()).convertAndSend(anyString(), any());
    }
}
