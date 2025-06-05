package com.zraj.tokenbackend.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisCodeService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.verification.code-ttl-minutes}")
    private int codeTtlMinutes;

    public RedisCodeService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRequest(String userId, WalletChangeRequestDTO request) {
        String key = "wallet_change:" + userId;
        redisTemplate.opsForValue().set(key, request);
        redisTemplate.expire(key, Duration.ofMinutes(codeTtlMinutes));
    }

    public WalletChangeRequestDTO getRequest(String userId) {
        return (WalletChangeRequestDTO ) redisTemplate.opsForValue().get("wallet_change:" + userId);
    }

    public void deleteRequest(String userId) {
        redisTemplate.delete("wallet_change:" + userId);
    }
}
