package com.yuri.rover.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public RedisService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addToSet(String wordKey, String urlValue) {
        redisTemplate.opsForSet().add(wordKey, urlValue);
    }

    public Set<String> getMembers(String chave) {
        return redisTemplate.opsForSet().members(chave);
    }
}
