package com.example.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisKeyValueAdapter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

//    @Bean
//    public RedisConnectionFactory connectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,
//                                                    LettucePoolingClientConfiguration lettucePoolConfig) {
//        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolConfig);
//    }
//
    @Bean
    @Primary
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

//    @Bean
//    public RedisStandaloneConfiguration redisStandaloneConfiguration(RedisProperties properties) {
//        return new RedisStandaloneConfiguration(properties.getHost(), properties.getPort());
//    }
//
//    @Bean
//    ClientOptions clientOptions() {
//        return ClientOptions.builder()
//                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
//                .autoReconnect(true)
//                .build();
//    }
//
//    @Bean
//    LettucePoolingClientConfiguration lettucePoolConfig(ClientOptions options, ClientResources clientResources) {
//        return LettucePoolingClientConfiguration.builder()
//                .clientName("gate-ws")
//                .poolConfig(new GenericObjectPoolConfig())
//                .clientOptions(options)
//                .clientResources(clientResources)
//                .build();
//    }
//
//    @Bean
//    public RedisConnectionFactory connectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,
//                                                    LettucePoolingClientConfiguration lettucePoolConfig) {
//        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettucePoolConfig);
//    }
//
//    @Bean
//    @Primary
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
//        return new StringRedisTemplate(connectionFactory);
//    }
}
