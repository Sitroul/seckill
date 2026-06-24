package io.github.sitroul.seckill.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Bean
    public Queue seckillOrderQueue() {
        return new Queue("seckill.order", true); // true=持久化
    }

    @Bean
    public JacksonJsonMessageConverter  jacksonJsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
