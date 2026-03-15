package com.chobbi.server.auth.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailBlacklistService {

    private static final String KEY_PREFIX = "auth:blacklist:email:";

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isBlacklisted(String email) {
        if (email == null) {
            return false;
        }
        String normalized = email.trim().toLowerCase();
        if (normalized.isEmpty()) {
            return false;
        }
        String key = KEY_PREFIX + normalized;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    public void addToBlacklist(String email) {
        if (email == null) {
            return;
        }
        String normalized = email.trim().toLowerCase();
        if (normalized.isEmpty()) {
            return;
        }
        String key = KEY_PREFIX + normalized;
        redisTemplate.opsForValue().set(key, Boolean.TRUE);
    }

    public void removeFromBlacklist(String email) {
        if (email == null) {
            return;
        }
        String normalized = email.trim().toLowerCase();
        if (normalized.isEmpty()) {
            return;
        }
        String key = KEY_PREFIX + normalized;
        redisTemplate.delete(key);
    }

    /** Lấy danh sách tất cả email đang trong blacklist. */
    public List<String> getAllBlacklistedEmails() {
        Set<String> keys = redisTemplate.keys(KEY_PREFIX + "*");
        List<String> emails = new ArrayList<>();
        if (keys == null || keys.isEmpty()) {
            return emails;
        }
        for (String key : keys) {
            if (key != null && key.startsWith(KEY_PREFIX)) {
                String email = key.substring(KEY_PREFIX.length());
                if (!email.isBlank()) {
                    emails.add(email);
                }
            }
        }
        return emails;
    }
}

