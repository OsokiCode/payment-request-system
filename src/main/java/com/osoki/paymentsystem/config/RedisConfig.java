package com.osoki.paymentsystem.config;

import com.osoki.paymentsystem.payment.dto.PaymentResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper; // Jackson 3 — NOT com.fasterxml

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, PaymentResponse> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper               // Spring Boot 4 provides this
    ) {
        RedisTemplate<String, PaymentResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        JacksonJsonRedisSerializer<PaymentResponse> jsonSerializer =
                new JacksonJsonRedisSerializer<>(objectMapper, PaymentResponse.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}